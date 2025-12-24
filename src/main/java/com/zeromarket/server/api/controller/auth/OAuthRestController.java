package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.KakaoLoginRequest;
import com.zeromarket.server.api.dto.auth.KakaoLinkRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.auth.OAuthService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "OAuth API", description = "OAuth API")
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OAuthRestController {

    private final OAuthService oAuthService;

    @Operation(summary = "oauth 로그인/회원가입", description = "")
    @PostMapping("/kakao")
    public ResponseEntity<?> login(
        @RequestBody KakaoLoginRequest request,
        HttpServletResponse response
    ) {
        String accessToken = oAuthService.loginWithKakao(request.getCode(), response);
        return ResponseEntity.ok(new TokenInfo(accessToken));
    }

    @Operation(summary = "카카오 계정 연동", description = "로그인 후 카카오 계정을 현재 계정에 연결")
    @PostMapping("/kakao/link")
    public ResponseEntity<?> linkKakao(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody KakaoLinkRequest request
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        oAuthService.linkKakao(request.getCode(), request.getRedirectUri(), userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카카오 계정 연동 해제", description = "로그인 후 카카오 연동을 해제")
    @PostMapping("/kakao/unlink")
    public ResponseEntity<?> unlinkKakao(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        oAuthService.unlinkKakao(userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}
