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

    // OAuth 카카오 로그인
    @Override
    @Transactional
    public String loginWithKakao(String code, HttpServletResponse response) {
        try {
            // 1. 카카오 액세스 토큰 발급
            String kakaoAccessToken = kakaoOAuthClient.requestToken(code);

            // 2. 카카오 사용자 정보 조회
            KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);

            // 3. DB에 없으면 생성 (기존 회원이면 반환)
            Member member = memberService.findOrCreateKakaoUser(userInfo);

            // 4. JWT 발급
            String accessToken = jwtUtil.generateAccessToken(member.getSocialId(), member.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(member.getSocialId());

            // 5. refresh 쿠키 저장
            jwtUtil.setRefreshCookie(refreshToken, response);

            // 6. 액세스 토큰 반환
            return accessToken;

        } catch (ApiException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void linkKakao(String code, String redirectUri, Long memberId) {
        String kakaoAccessToken = kakaoOAuthClient.requestToken(code, redirectUri);
        KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);
        String socialId = "kakao_" + userInfo.getId();

        memberService.linkSocial(memberId, socialId);
    }

    @Override
    @Transactional
    public void unlinkKakao(Long memberId) {
        Member member = memberMapper.selectMemberById(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
        }

        String socialId = member.getSocialId();
        if (socialId != null && socialId.startsWith("kakao_")) {
            String extracted = socialId.substring("kakao_".length());
            kakaoOAuthClient.unlinkWithAdminKey(extracted);
        }

        memberService.unlinkSocial(memberId);
    }
}
