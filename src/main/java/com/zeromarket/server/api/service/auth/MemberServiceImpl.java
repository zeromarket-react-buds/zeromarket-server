package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.WithdrawRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.WishSellerMapper;
import com.zeromarket.server.api.mapper.trade.TradeHistoryMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.api.security.KakaoOAuthClient;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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
    private final TradeHistoryMapper tradeHistoryMapper;
    private final JwtUtil jwtUtil;
    private final KakaoOAuthClient kakaoOAuthClient;

    // 회원 프로필 조회 (타겟 프로필)
    @Override
    public MemberProfileDto getMemberProfile(Long memberId, Long authMemberId) {
        // 프로필 조회
        MemberProfileDto dto = memberMapper.selectMemberProfile(memberId);
        if (dto == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        // 신뢰도 조회
        double trustScore = reviewService.getTrustScore(memberId);
        dto.setTrustScore(Double.toString(trustScore));

        boolean liked = false;

        //로긴 상태일때만 디비에서 좋아요 여부 조회
        if(authMemberId != null){
            WishSellerDto wishSellerDto = wishSellerMapper.selectWishSeller(authMemberId,memberId);
            if(wishSellerDto != null && Boolean.FALSE.equals(wishSellerDto.getIsDeleted())){
                liked=true;
            }
        }

        dto.setLiked(liked);

        return dto;
    }

    // 회원정보 설정 페이지 조회용 회원 조회
    @Override
    public MemberEditResponse getMemberEdit(Long memberId) {
        return memberMapper.getMemberEdit(memberId);
    }

    // 회원정보 설정 페이지 수정
    @Override
    public MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request) {
        memberMapper.updateMemberEdit(memberId, request);

            // 수정 후 최신 정보 다시 조회하여 반환
            return memberMapper.getMemberEdit(memberId);
    }

    @Transactional
    public Member findOrCreateKakaoUser(KakaoUserInfo kakaoUserInfo) {

        // 1. social_id 생성
        String socialId = "kakao_" + kakaoUserInfo.getId();

        // 2. 소셜 가입자 여부 조회 (탈퇴 포함)
        Member member = memberMapper.findBySocialIdWithWithdrawn(socialId);
        if (member != null) {
            if (member.getWithdrawnAt() != null) {
                LocalDateTime withdrawnAt = member.getWithdrawnAt();
                // 7일 이내 재가입 제한
                if (withdrawnAt.plusDays(7).isAfter(LocalDateTime.now())) {
                    throw new ApiException(ErrorCode.REJOIN_NOT_ALLOWED_YET);
                }
                // 7일 경과 시 새로 가입 허용 → 기존 탈퇴 계정은 그대로 두고 신규 생성으로 진행
            }
            return member;
        }

        // 3. 프로필 정보 추출 (null 허용)
        KakaoUserInfo.KakaoAccount account = kakaoUserInfo.getKakao_account();
        KakaoUserInfo.KakaoAccount.Profile profile =
            account != null ? account.getProfile() : null;

        String nicknameBase = profile != null ? profile.getNickname() : "제로마켓유저";
        String profileImageUrl =
            profile != null ? profile.getProfile_image_url() : null;

        log.error("회원 가입 profileImageUrl={}", profileImageUrl);

        int maxRetry = 5;

        // nickname unique 제약 준수를 위해 최대 5회까지 재시도
        for(int i = 0; i < maxRetry; i++) {
            String suffix = UUID.randomUUID().toString().substring(0, 4);
            String nickname = nicknameBase + "_" + suffix;

            // 4. 회원 생성
            Member newMember = new Member();
            newMember.setSocialId(socialId);
            newMember.setNickname(nickname);
            newMember.setProfileImage(profileImageUrl);
            newMember.setRole(Role.ROLE_USER.getDescription());
            newMember.setLoginId(socialId);                 // 소셜 가입자 - loginId
            newMember.setPassword("{noop}SOCIAL_LOGIN");    // 소셜 가입자 - password

            try {
                memberMapper.insertSocialMember(newMember);

//                log.info("loginId = null 확인: {}",  newMember.getLoginId());

                return newMember;
            } catch(DuplicateKeyException e) {
                // nickname UNIQUE 충돌 발생 시 재시도
                if (i == maxRetry - 1) {
                    throw new IllegalStateException("닉네임 생성에 실패했습니다. 다시 시도해 주세요.", e);
                }
            }
        }

        // 예외가 누락된 경우 방어 로직
        throw new IllegalStateException("닉네임 생성 실패");
    }

    // TODO: 일반 로그인 탈퇴 고려!(oauth 회원만 고려) 및 알람 정리 (추후 연동 로직 추가)
    // 회원탈퇴
    @Override
    public void withdraw(Long memberId, WithdrawRequest request, HttpServletResponse response) {

        // 탈퇴 여부와 무관하게 조회
        Member member = memberMapper.selectMemberByIdWithWithdrawn(memberId);
        if(member == null) {throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        // Idempotent(멱등 처리): already withdrawn -> 외부 호출/DB 업데이트 없이 쿠키만 삭제 후 200 반환
        if (member.getWithdrawnAt() != null) {
            jwtUtil.setRefreshCookie(null, response);
            return;
        }

        // 진행 중 거래(PENDING) 존재 여부 검사
        boolean hasActiveTrade = tradeHistoryMapper.existsActiveTradeByMemberId(memberId);
        if (hasActiveTrade) {
            throw new ApiException(ErrorCode.CANNOT_WITHDRAW_DURING_ACTIVE_TRADE);
        }

        // 1. 카카오 연동 해제 (socialId != null인 경우)
        String socialId = member.getSocialId();
        String prefix = "kakao_";

        if (socialId != null && socialId.startsWith(prefix)) {
            String extracted = socialId.substring(prefix.length());
            log.info("탈퇴 대상 socialId: {}", extracted);

            kakaoOAuthClient.unlinkWithAdminKey(extracted);
            log.info("카카오 연동 해제 완료");
        }

        // 2. DB 회원 상태 업데이트 (soft delete)
        int updated = memberMapper.withdrawMember(
            memberId,
            request != null ? request.getWithdrawalReasonId() : null,
            request != null ? request.getWithdrawalReasonDetail() : null
        );

        if (updated == 0) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        log.info("회원탈퇴 완료, 업데이트된 row: {}", updated);

        // 3. 리프레시 삭제
        jwtUtil.setRefreshCookie(null, response);
    }

    @Override
    public void logout(HttpServletResponse response) {
//        1. 리프레시 삭제
        jwtUtil.setRefreshCookie(null, response);

    }
}
