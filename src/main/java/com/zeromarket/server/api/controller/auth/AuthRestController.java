package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.service.auth.MemberService;
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

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody MemberSignupRequest dto) {
        Long memberId = memberService.signup(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "회원가입 성공", "memberId", memberId));

    }

    @Operation(summary = "로그인", description = "")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
        @RequestBody MemberLoginRequest dto,
        HttpServletResponse response
    ) {
        TokenInfo tokenInfo = memberService.login(dto);

        // ✅ HttpOnly Cookie에 refresh token 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
            .httpOnly(true)
            .secure(false)           // https 환경이면 true 로 변경
            .path("/")              // 모든 요청에 포함
            .maxAge(Duration.ofDays(7)) // 7일
            .sameSite("Strict")     // 또는 Lax, 같은 도메인/포트 조합이면 Strict도 무방
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // ✅ body에는 access token만 보냄
        return ResponseEntity.ok(
            Map.of("accessToken", tokenInfo.getAccessToken())
        );
    }

    @Operation(summary = "엑세스 토큰 재발급 (refresh token flow)", description = "")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
        @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        TokenInfo tokens = memberService.refresh(refreshToken);
//        TokenInfo tokenInfo = memberService.refresh(refreshToken);

        // refreshToken 재발급 시 쿠키도 재설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
            .httpOnly(true)
            .secure(false)
//            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(Duration.ofDays(7))
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("accessToken", tokens.getAccessToken()));
//        return ResponseEntity.ok(tokenInfo);
    }

    @Operation(summary = "아이디 중복 체크", description = "")
    @GetMapping("/check-id")
    public ResponseEntity<Map> checkDuplicateId(@RequestParam String loginId) {
        Boolean existsByLoginId = memberService.checkDuplicateId(loginId);

        return ResponseEntity.ok(Map.of("existsByLoginId", existsByLoginId));
    }

    @Operation(summary = "로그아웃", description = "")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // 같은 이름 + path + maxAge=0 으로 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false) // 개발 시
//            .secure(true) // 운영 서버 배포 시
            .path("/")
            .maxAge(0)
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok().build();
    }
}
