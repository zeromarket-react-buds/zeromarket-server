package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.dto.auth.FindAccountResponse;
import com.zeromarket.server.api.dto.auth.FindAccountRequest;
import com.zeromarket.server.api.service.auth.AuthService;
import com.zeromarket.server.api.service.auth.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthRestController
 * - signup         /api/auth/signup
 * - login          /api/auth/login
 * - find-id        /api/auth/find-id
 * - find-password  /api/auth/find-password
 * - refresh        /api/auth/refresh
 * - check-id       /api/auth/check-id
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증/인가 API")
public class AuthRestController {

    private final AuthService authService;
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
        TokenInfo tokenInfo = authService.login(dto, response);
        return ResponseEntity.ok(
            Map.of("accessToken", tokenInfo.getAccessToken())
        );
    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기용 회원 정보 조회")
    @GetMapping("/find-id")
    public ResponseEntity<FindAccountResponse> findLoginId(
        @ModelAttribute FindAccountRequest findAccountRequest
    ) {

        FindAccountResponse result =
            authService.findLoginId(findAccountRequest);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 찾기용 회원 정보 조회")
    @GetMapping("/find-password")
    public ResponseEntity<Void> findPassword(
        @ModelAttribute FindAccountRequest request
    ) {
        authService.findPassword(request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "새 비밀번호 변경", description = "기존 비밀번호를 새 비밀번호 변경")
    @PostMapping("/set-password")
    public ResponseEntity<Void> setPassword(
        @RequestBody FindAccountRequest request
    ) {
        authService.setPassword(request);

        return ResponseEntity.ok().build();
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
        Boolean existsByLoginId = memberService.checkDuplicateId(loginId);
        return ResponseEntity.ok(Map.of("existsByLoginId", existsByLoginId));
    }
}
