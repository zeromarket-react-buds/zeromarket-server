package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.auth.MemberService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 API")
public class MemberRestController {

    private final MemberService memberService;

    //    인증 관련 사용 (인증 정보 조회 API)
    @Operation(summary = "내 정보 조회", description = "")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if(userDetails == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        MemberResponse response = memberService.getMyInfo(userDetails.getLoginId());

        return ResponseEntity.ok(response);
    }
}
