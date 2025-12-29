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
import com.zeromarket.server.api.security.KakaoOAuthClient;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServicee
 * - login
 * - logout
 * - refresh
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public TokenInfo login(MemberLoginRequest dto, HttpServletResponse response) {
        // loginId으로 회원 찾기 (탈퇴 포함)
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginIdWithWithdrawn(dto.getLoginId()))
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        // 탈퇴 계정 여부 검사
        if (member.getWithdrawnAt() != null) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAIL);
        }

        // 쿠키 설정
        jwtUtil.setRefreshCookie(
                jwtUtil.generateRefreshToken(dto.getLoginId()),
                response
        );

        // 엑세스 토큰 반환
        return new TokenInfo(jwtUtil.generateAccessToken(member.getLoginId(), member.getRole())
        );
    }
    
    @Override
    public void logout(HttpServletResponse response) {
//        1. 리프레시 삭제
        jwtUtil.setRefreshCookie(null, response);

    }

    @Override
    @Transactional(readOnly = true)
    public TokenInfo refresh(String refreshToken, HttpServletResponse response) {

//        1. null 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.JWT_NOT_EXIST);
        }

//        2. token 유효 확인
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ApiException(ErrorCode.JWT_NOT_VALID);
        }

//        3. token 재발급
        String loginId = jwtUtil.getLoginId(refreshToken);

        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginIdWithWithdrawn(loginId))
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.getWithdrawnAt() != null) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(member.getLoginId(), member.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getLoginId());

//        4. 쿠키 설정
        jwtUtil.setRefreshCookie(
                newRefreshToken,
                response
        );

        return new TokenInfo(newAccessToken);
    }
}
