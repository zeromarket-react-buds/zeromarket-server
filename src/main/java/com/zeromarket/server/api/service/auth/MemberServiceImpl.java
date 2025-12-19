package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.WishSellerMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.api.security.KakaoOAuthClient;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ReviewService reviewService;
    private final WishSellerMapper wishSellerMapper;
    private final JwtUtil jwtUtil;
    private final KakaoOAuthClient kakaoOAuthClient;

    // 회원 프로필 정보 조회 (셀러샵 사용)
    @Override
    public MemberProfileDto getMemberProfile(Long memberId, Long authMemberId) {
        // 프로필 정보 조회
        MemberProfileDto dto = memberMapper.selectMemberProfile(memberId);
        if (dto == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        // 신뢰점수 추가
        double trustScore = reviewService.getTrustScore(memberId);
        dto.setTrustScore(Double.toString(trustScore));

        // 좋아요 여부 추가
        WishSellerDto wishSellerDto = wishSellerMapper.selectWishSeller(authMemberId, memberId);
        boolean liked = false;
        if(wishSellerDto != null && Boolean.FALSE.equals(wishSellerDto.getIsDeleted())) {
            liked = true;
        };
        dto.setLiked(liked);

        return dto;
    }

    // 회원정보 설정 페이지에서 해당 회원 정보 조회
    @Override
    public MemberEditResponse getMemberEdit(Long memberId) {
        return memberMapper.getMemberEdit(memberId);
    }

    // 회원정보 설정 페이지에서 해당 회원 정보 수정
    @Override
    public MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request) {
        memberMapper.updateMemberEdit(memberId, request);

            // 수정 후 최신 값 다시 조회해서 반환
            return memberMapper.getMemberEdit(memberId);
    }

    @Transactional
    public Member findOrCreateKakaoUser(KakaoUserInfo kakaoUserInfo) {

        // 1. social_id 생성
        String socialId = "kakao_" + kakaoUserInfo.getId();

        // 2. 이미 가입된 회원 조회
        Member member = memberMapper.findBySocialId(socialId);
        if (member != null) {
            return member;
        }

        // 3. 안전하게 값 꺼내기 (null 방어)
        KakaoUserInfo.KakaoAccount account = kakaoUserInfo.getKakao_account();
        KakaoUserInfo.KakaoAccount.Profile profile =
            account != null ? account.getProfile() : null;

        String nicknameBase = profile != null ? profile.getNickname() : "카카오사용자";
        String profileImageUrl =
            profile != null ? profile.getProfile_image_url() : null;

        log.error("가져온 profileImageUrl={}", profileImageUrl);

        int maxRetry = 5;

        // nickname unique 제약 조건 위반 시, 5번까지 재생성 시도
        for(int i = 0; i < maxRetry; i++) {
            String suffix = UUID.randomUUID().toString().substring(0, 4);
            String nickname = nicknameBase + "_" + suffix;

            // 4. 신규 회원 생성
            Member newMember = new Member();
            newMember.setSocialId(socialId);
            newMember.setNickname(nickname);
            newMember.setProfileImage(profileImageUrl);
            newMember.setRole(Role.ROLE_USER.getDescription());
            newMember.setLoginId(socialId);                 // 더미 데이터 - loginId
            newMember.setPassword("{noop}SOCIAL_LOGIN");    // 더미 데이터 - password

            try {
                memberMapper.insertSocialMember(newMember);

//                log.info("loginId = null 확인용: {}",  newMember.getLoginId());

                return newMember;
            } catch(DuplicateKeyException e) {
                // nickname UNIQUE 충돌 → 다시 시도
                if (i == maxRetry - 1) {
                    throw new IllegalStateException("닉네임 생성에 실패했습니다. 잠시 후 다시 시도해주세요.", e);
                }
            }
        }

        // 논리적으로 도달하지 않음
        throw new IllegalStateException("닉네임 생성 실패");
    }

    // TODO: oauth 계정 탈퇴만 고려 (일반 계정 고려 필요)
    // 회원탈퇴
    @Override
    public void withdraw(Long memberId, HttpServletResponse response) {

        Member member = memberMapper.selectMemberById(memberId);
        if(member == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        // 1. 카카오 연결 해제 (socialId != null인 경우)
        String socialId = member.getSocialId();
        String prefix = "kakao_";

        if (socialId != null && socialId.startsWith(prefix)) {
            String extracted = socialId.substring(prefix.length());
            log.info("추출된 socialId: {}", extracted);

            kakaoOAuthClient.unlinkWithAdminKey(extracted);
            log.info("카카오 연결 해제 완료");
        }

        // 2. 쿠키 해제
        jwtUtil.setRefreshCookie(null, response);

        // 3. DB 회원 정보 삭제 (소프트 딜리트)
        int updated = memberMapper.withdrawMember(
            memberId,
            2,
            ""
        );

        if (updated == 0) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        log.info("탈퇴 성공, 변경된 row: {}", updated);
    }

    @Override
    public void logout(HttpServletResponse response) {
//        1. 쿠키 해제
        jwtUtil.setRefreshCookie("", response);

    }
}
