package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberService    (member 관련 CRUD) 
 * 조회
 * - getMyInfo
 * - getMemberProfile
 * - getMemberEdit
 * - checkDuplicateId
 * 생성
 * - signup
 * 수정
 * - updateMemberEdit
 * 삭제
 * - withdraw
 */

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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(String loginId) {
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginIdWithWithdrawn(loginId))
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.getWithdrawnAt() != null) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        MemberResponse response = new MemberResponse();

        BeanUtils.copyProperties(member, response);
        response.setSocialLinked(member.getSocialId() != null);

        return response;
    }

    // 회원 프로필 조회 (타겟 프로필)
    @Override
    public MemberProfileDto getMemberProfile(Long memberId, Long authMemberId) {
        // 프로필 조회
        MemberProfileDto dto = memberMapper.selectMemberProfile(memberId);
        if (dto == null)
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        // 신뢰도 조회
        double trustScore = reviewService.getTrustScore(memberId);
        dto.setTrustScore(Double.toString(trustScore));

        boolean liked = false;

        //로긴 상태일때만 디비에서 좋아요 여부 조회
        if (authMemberId != null) {
            WishSellerDto wishSellerDto = wishSellerMapper.selectWishSeller(authMemberId, memberId);
            if (wishSellerDto != null && Boolean.FALSE.equals(wishSellerDto.getIsDeleted())) {
                liked = true;
            }
        }

        dto.setLiked(liked);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkDuplicateId(String loginId) {
        return memberMapper.existsByLoginId(loginId);
    }

    // 회원정보 설정 페이지 조회용 회원 조회
    @Override
    public MemberEditResponse getMemberEdit(Long memberId) {
        return memberMapper.getMemberEdit(memberId);
    }

    @Transactional
    public Long signup(MemberSignupRequest dto) {
//        1. email 필드가 빈 문자열("")인 경우, Null 값으로 변경(DB 저장을 위해)
        String email = dto.getEmail();
        if (email == null || email.isBlank()) {
            dto.setEmail(null);
        }

//        2. unique 필드 검증
        if (memberMapper.existsByLoginId(dto.getLoginId())) {
            throw new ApiException(ErrorCode.LOGINID_ALREADY_EXIST);
        }

        if (memberMapper.existsByNickname(dto.getNickname())) {
            throw new ApiException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }

        if (memberMapper.existsByPhone(dto.getPhone())) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXIST);
        }

        if (dto.getEmail() != null && memberMapper.existsByEmail(dto.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

//        3. dto -> entity
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setRole(Role.ROLE_USER.getDescription());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. DB insert
        try {
            memberMapper.insertMember(member);
            return member.getMemberId();
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE);
        }
    }

    // 회원정보 설정 페이지 수정
    @Override
    public MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request) {
        memberMapper.updateMemberEdit(memberId, request);

        // 수정 후 최신 정보 다시 조회하여 반환
        return memberMapper.getMemberEdit(memberId);
    }

    // TODO: 알람 정리 (추후 연동 로직 추가)
    // 회원탈퇴
    @Override
    public void withdraw(Long memberId, WithdrawRequest request, HttpServletResponse response) {

        // 0. 멱등 처리 & 거래 중 존재 여부 검사
        // 0-1. 탈퇴 여부와 무관하게 조회
        Member member = memberMapper.selectMemberByIdWithWithdrawn(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // Idempotent(멱등 처리): already withdrawn -> 외부 호출/DB 업데이트 없이 쿠키만 삭제 후 200 반환
        if (member.getWithdrawnAt() != null) {
            jwtUtil.setRefreshCookie(null, response);
            return;
        }

        // 0-2. 진행 중 거래(PENDING) 존재 여부 검사
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
                request != null ? request.getWithdrawalReasonDetail() : null);

        if (updated == 0) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        log.info("회원탈퇴 완료, 업데이트된 row: {}", updated);

        // 3. 쿠키(refresh token) 삭제
        jwtUtil.setRefreshCookie(null, response);
    }
}
