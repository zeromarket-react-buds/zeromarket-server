package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.api.security.KakaoOAuthClient;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    // OAuth 로그인/회원가입
    @Transactional
    @Override
    public String loginWithKakao(String code, HttpServletResponse response) {
        try {
//        1. 카카오 엑세스 토큰 발급
        String kakaoAccessToken = kakaoOAuthClient.requestToken(code);

//        2. 사용자 정보 조회 with 카카오 엑세스 토큰
        KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);

//        3. DB에서 유저 정보 불러오기 (로그인 또는 회원가입)
        Member member = memberService.findOrCreateKakaoUser(userInfo);

//        4. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getSocialId(), member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(member.getSocialId());

//        5. refresh 쿠키 설정
        jwtUtil.setRefreshCookie(refreshToken, response);

//        6. 엑세스 토큰 반환
        return accessToken;

        } catch (ApiException e) {
            throw e;
        }
    }
}
