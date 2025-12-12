package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
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

//    프로필 정보 조회 (셀러샵 사용)
    @Operation(summary = "프로필 정보 조회", description = "")
    @GetMapping("{memberId}/profile")
    public ResponseEntity<MemberProfileDto> getMyProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long memberId
    ) {
        MemberProfileDto dto = memberService.getMemberProfile(
            memberId,                   // 셀러샵 회원
            userDetails.getMemberId()   // 로그인 회원 (좋아요 확인용)
        );
        return ResponseEntity.ok(dto);
    }

    // 회원정보 설정 페이지 정보 조회
    @Operation(summary = "회원정보 설정 페이지 조회", description = "회원정보 설정 페이지 멤버 조회")
    @GetMapping("me/edit")
    public MemberEditResponse getMemberEdit(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        Long memberId = userPrincipal.getMemberId();
        return memberService.getMemberEdit(memberId);
    }
}
