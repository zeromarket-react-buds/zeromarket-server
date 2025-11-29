package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 API")
public class MemberRestController {

    private final MemberService memberService;

//   getMyInfo 만들어서 authcontext + jwt 테스트 --> refreshtoken 쿠키에 저장하기 11/24(월)
//    인증 관련 사용
    @Operation(summary = "내 정보 조회", description = "")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo() {
       MemberResponse response = memberService.getMyInfo();

        return ResponseEntity.ok(response);
    }
}
