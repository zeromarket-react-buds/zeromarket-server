package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.KakaoLoginRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.service.auth.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuthRestController
 * - oauthLogin     /api/oauth/kakao
 */

@Tag(name = "OAuth API", description = "OAuth API")
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OAuthRestController {

    private final OAuthService oAuthService;

    @Operation(summary = "카카오 로그인", description = "카카오 인가코드로 로그인/회원가입 후 토큰 발급")
    @PostMapping("/kakao")
    public ResponseEntity<TokenInfo> oauthLogin(
        @RequestBody KakaoLoginRequest request,
        HttpServletResponse response
    ) {
        String accessToken = oAuthService.loginWithKakao(request.getCode(), response);
        return ResponseEntity.ok(new TokenInfo(accessToken));
    }
}
