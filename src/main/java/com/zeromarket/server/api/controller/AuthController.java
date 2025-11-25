package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AuthController {

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
    public ResponseEntity<TokenInfo> login(@RequestBody MemberLoginRequest dto) {
        TokenInfo tokenInfo = memberService.login(dto);

        return ResponseEntity.ok(tokenInfo);
    }

    @Operation(summary = "엑세스 토큰 재발급 (refresh token flow)", description = "")
    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refresh(@RequestBody Map<String, String> body) {
        TokenInfo tokenInfo = memberService.refresh(body.get("refreshToken"));

        return ResponseEntity.ok(tokenInfo);
    }

    @Operation(summary = "아이디 중복 체크", description = "")
    @GetMapping("/check-id")
    public ResponseEntity<Map> checkDuplicateId(@RequestParam String loginId) {
        Boolean existsByLoginId = memberService.checkDuplicateId(loginId);

        return ResponseEntity.ok(Map.of("existsByLoginId", existsByLoginId));
    }
}
