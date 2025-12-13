package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.WishSellerMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ReviewService reviewService;
    private final WishSellerMapper wishSellerMapper;
    private final OAuthLoginService kakaoOAuthService;
    private final MemberService memberService;

    @Override
    @Transactional
    public Long signup(MemberSignupRequest dto) {
//        email 필드가 빈 문자열("")인 경우, Null 값으로 변경(DB 저장을 위해)
        String email = dto.getEmail();
        if(email == null || email.isBlank()) {
            dto.setEmail(null);
        }

//        unique 필드 검증
        if(memberMapper.existsByLoginId(dto.getLoginId())) {
            throw new ApiException(ErrorCode.LOGINID_ALREADY_EXIST);
        }

        if(memberMapper.existsByNickname(dto.getNickname())) {
            throw new ApiException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }

        if(memberMapper.existsByPhone(dto.getPhone())) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXIST);
        }

        if(dto.getEmail() != null && memberMapper.existsByEmail(dto.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

//        dto -> entity
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setRole(Role.ROLE_USER.getDescription());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        try {
            memberMapper.insertMember(member);
            return member.getMemberId();
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public TokenInfo login(MemberLoginRequest dto) {
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(dto.getLoginId()))
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAIL);
        }

        return new TokenInfo(
            jwtUtil.generateAccessToken(member.getLoginId(), member.getRole()),
            jwtUtil.generateRefreshToken(member.getLoginId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TokenInfo refresh(String refreshToken) {

//        1. null 확인
        if (refreshToken == null|| refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        }

//        2. access token 유효 확인
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ApiException(ErrorCode.JWT_NOT_VALID);
        }

//        3. token 재발급
        String loginId = jwtUtil.getLoginId(refreshToken);

        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(loginId))
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(member.getLoginId(), member.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getLoginId());

        return new TokenInfo(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(String loginId) {
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(loginId))
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        MemberResponse response = new MemberResponse();

        BeanUtils.copyProperties(member, response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkDuplicateId(String loginId) {
        boolean existsByLoginId = memberMapper.existsByLoginId(loginId);

        return  existsByLoginId;
    }

    // 회원 프로필 정보 조회 (셀러샵 사용)
    @Override
    public MemberProfileDto  getMemberProfile(Long memberId, Long authMemberId) {
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

    // OAuth 로그인/회원가입
    @Transactional
    @Override
    public String loginWithKakao(String code, HttpServletResponse response) {
//        1. 카카오 엑세스 토큰 발급
        String kakaoAccessToken = kakaoOAuthService.getAccessToken(code);

//        2. 사용자 정보 조회 with 카카오 엑세스 토큰
        KakaoUserInfo userInfo = kakaoOAuthService.getUserInfo(kakaoAccessToken);

//        3. DB에서 유저 정보 불러오기 (없으면 생성)
//        TODO: 왜 loginId가 null이지?
        Member member = memberService.findOrCreateKakaoUser(userInfo);
        System.out.println(member.getLoginId()); // null
        System.out.println(member.getSocialId()); // kakao_4637921436

//        4. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getSocialId(), member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(member.getLoginId());

//        5. refresh 쿠키 설정
        jwtUtil.setRefreshCookie(refreshToken, response);

//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + accessToken);

//        6. 엑세스 토큰 반환
        return accessToken;
    }
}
