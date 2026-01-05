package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.api.security.KakaoOAuthClient;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuthService
 * - loginWithKakao
 *      - findOrCreateKakaoUser
 *      - ensureUniqueLoginId
 *      - ensureUniqueNickname
 * - linkKakao
 * - unlinkKakao
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final JwtUtil jwtUtil;
    private final MemberMapper memberMapper;

    // OAuth 카카오 로그인
    @Override
    @Transactional
    public String loginWithKakao(String code, HttpServletResponse response) {
        try {
            // 1. 카카오 액세스 토큰 발급 & 카카오 사용자 정보 조회
            String kakaoAccessToken = kakaoOAuthClient.requestToken(code);
            KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);

            // 2. DB에 없으면 생성 (기존 회원이면 반환)
            Member member = findOrCreateKakaoUser(userInfo);

            // 3. JWT 발급
            String accessToken = jwtUtil.generateAccessToken(member.getSocialId(), member.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(member.getSocialId());

            // 3-1. refresh 쿠키 저장
            jwtUtil.setRefreshCookie(refreshToken, response);

            // 3-2. 액세스 토큰 반환
            return accessToken;

        } catch (ApiException e) {
            throw e;
        }
    }

    // member 데이터 조회 및 생성 (OAuth 로그인/회원가입 시)
    @Transactional
    private Member findOrCreateKakaoUser(KakaoUserInfo kakaoUserInfo) {

        // 1. social_id 생성
        String socialId = "kakao_" + kakaoUserInfo.getId();

        log.info("uk_member_login_id 관련 에러 해결용: {}", socialId);

        // 2. 소셜 계정 존재 (oauth 로그인)
        Member member = memberMapper.findBySocialIdWithWithdrawn(socialId);
        if (member != null) {
            // 3. 탈퇴 상태
            if (member.getWithdrawnAt() != null) {
                LocalDateTime withdrawnAt = member.getWithdrawnAt();
                // 3-1. 7일 이내 (재가입 제한)
                if (withdrawnAt.plusDays(7).isAfter(LocalDateTime.now())) {
                    throw new ApiException(ErrorCode.REJOIN_NOT_ALLOWED_YET);
                }
                // 3-2. 7일 경과 (기존 계정 재활성화) (INSERT 없이 UPDATE)
                String newLoginId = ensureUniqueLoginId(socialId);
                String nicknameBase = Optional.ofNullable(kakaoUserInfo.getKakao_account())
                        .map(KakaoUserInfo.KakaoAccount::getProfile)
                        .map(KakaoUserInfo.KakaoAccount.Profile::getNickname)
                        .orElse("제로마켓유저");
                String newNickname = ensureUniqueNickname(nicknameBase);
                String newProfileImage = Optional.ofNullable(kakaoUserInfo.getKakao_account())
                        .map(KakaoUserInfo.KakaoAccount::getProfile)
                        .map(KakaoUserInfo.KakaoAccount.Profile::getProfile_image_url)
                        .orElse(member.getProfileImage());

                memberMapper.reactivateMember(
                        member.getMemberId(),
                        newLoginId,
                        newNickname,
                        newProfileImage,
                        socialId
                    );

                member.setLoginId(newLoginId);
                member.setNickname(newNickname);
                member.setProfileImage(newProfileImage);
                member.setWithdrawnAt(null);
                member.setWithdrawalReasonId(null);
                member.setWithdrawalReasonDetail(null);
            }
            return member;
        }

        // 4. 소셜 계정 없음 (oauth 가입)
        KakaoUserInfo.KakaoAccount account = kakaoUserInfo.getKakao_account();
        KakaoUserInfo.KakaoAccount.Profile profile = account != null ? account.getProfile() : null;

        String nicknameBase = profile != null ? profile.getNickname() : "제로마켓유저";
        String profileImageUrl = profile != null ? profile.getProfile_image_url() : null;

        // 회원 생성
        Member newMember = new Member();
        newMember.setSocialId(socialId);
        newMember.setLoginId(socialId); // 소셜 가입자 - loginId
        newMember.setPassword("{noop}SOCIAL_LOGIN"); // 소셜 가입자 - password
        newMember.setNickname(ensureUniqueNickname(nicknameBase));
        newMember.setProfileImage(profileImageUrl);
        newMember.setRole(Role.ROLE_USER.getDescription());

        memberMapper.insertSocialMember(newMember);

        return newMember;
    }
    
    private String ensureUniqueLoginId(String base) {
        String candidate = base;
        while (true) {
            boolean exists = memberMapper.existsByLoginId(candidate);
            if (!exists) return candidate;
            String suffix = UUID.randomUUID().toString().substring(0, 4);
            candidate = base + "_" + suffix;
        }
    }

    private String ensureUniqueNickname(String base) {
        String candidate = base;
        while (true) {
            boolean exists = memberMapper.existsByNickname(candidate);
//            boolean exists = memberMapper.existsByNicknameExcludingMe(candidate, memberId);
            if (!exists)
                return candidate;
            String suffix = UUID.randomUUID().toString().substring(0, 4);
            candidate = base + "_" + suffix;
        }
    }

    // 카카오 계정 연동
    @Override
    @Transactional
    public void linkKakao(String code, String redirectUri, Long memberId) {
//        1. 카카오 엑세스 토큰, 회원 정보 요청
        String kakaoAccessToken = kakaoOAuthClient.requestToken(code, redirectUri);
        KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);
        String socialId = "kakao_" + userInfo.getId();
        
//        2. 유효성 검사
        Member me = memberMapper.selectMemberById(memberId);
        if (me == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 연동된 카카오 존재
        if (me.getSocialId() != null) {
            throw new ApiException(ErrorCode.SOCIAL_LINK_NOT_ALLOWED);
        }

        // 다른 회원이 해당 socialId를 사용하는 경우 -> 연동 불가
        Member other = memberMapper.findBySocialId(socialId);
        if (other != null && !other.getMemberId().equals(memberId)) {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE);
        }

//       3. social_id 업데이트
        memberMapper.updateSocialId(memberId, socialId);
    }

    // 카카오 계정 연동 해제
    @Override
    @Transactional
    public void unlinkKakao(Long memberId) {
//        1. 유효성 검사
        Member member = memberMapper.selectMemberById(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        String socialId = member.getSocialId();
        if (socialId != null && socialId.startsWith("kakao_")) {
            String extracted = socialId.substring("kakao_".length());
            kakaoOAuthClient.unlinkWithAdminKey(extracted);
        }

        Member me = memberMapper.selectMemberById(memberId);
        if (me == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

//        social_id 업데이트
        memberMapper.updateSocialId(memberId, null);
    }
}
