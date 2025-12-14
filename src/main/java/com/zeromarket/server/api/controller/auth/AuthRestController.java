package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증/인가 API")
public class AuthRestController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody MemberSignupRequest dto) {
        Long memberId = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "회원가입 성공", "memberId", memberId));

    }

    @Operation(summary = "로그인", description = "")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
        @RequestBody MemberLoginRequest dto,
        HttpServletResponse response
    ) {
        TokenInfo tokenInfo = authService.login(dto, response);
        return ResponseEntity.ok(
            Map.of("accessToken", tokenInfo.getAccessToken())
        );
    }

    @Operation(summary = "엑세스 토큰 재발급 (refresh token flow)", description = "")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
        @CookieValue(value = "refreshToken", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        TokenInfo tokens = authService.refresh(refreshToken, response);
        return ResponseEntity.ok()
            .body(Map.of("accessToken", tokens.getAccessToken()));
    }

    @Operation(summary = "아이디 중복 체크", description = "")
    @GetMapping("/check-id")
    public ResponseEntity<Map> checkDuplicateId(@RequestParam String loginId) {
        Boolean existsByLoginId = authService.checkDuplicateId(loginId);
        return ResponseEntity.ok(Map.of("existsByLoginId", existsByLoginId));
    }

    @Operation(summary = "로그아웃", description = "")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }
}
