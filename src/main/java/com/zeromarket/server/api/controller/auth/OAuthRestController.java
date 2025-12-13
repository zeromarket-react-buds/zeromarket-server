package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.KakaoLoginRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OAuthRestController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(
        @RequestBody KakaoLoginRequest request,
        HttpServletResponse response
    ) {
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>> kakaoLogin <<<<<<<<<<<<<<<<<<<<<<");

        String accessToken = authService.loginWithKakao(request.getCode(), response);
        return ResponseEntity.ok(new TokenInfo(accessToken));
    }
}
