package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.api.service.MemberService;
import com.zeromarket.server.common.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
//        return ResponseEntity.ok(Map.of(
//            "accessToken", accessToken,
//            "refreshToken", refreshToken
//        ));
    }
}
