package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.api.dto.mypage.ProfileSettingRequest;
import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.auth.AuthService;
import com.zeromarket.server.api.service.auth.MemberService;
import com.zeromarket.server.api.service.auth.OAuthService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 API")
public class MemberRestController {

    private final AuthService authService;
    private final MemberService memberService;

    //    인증 관련 사용 (인증 정보 조회 API)
    @Operation(summary = "내 정보 조회", description = "")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if(userDetails == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        MemberResponse response = authService.getMyInfo(userDetails.getLoginId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        memberService.logout(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        HttpServletResponse response
    ) {
        if(userDetails == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        memberService.withdraw(userDetails.getMemberId(), response);
        return ResponseEntity.ok().build();
    }

//    프로필 정보 조회 (셀러샵 사용)
    @Operation(summary = "프로필 정보 조회", description = "")
    @GetMapping("{memberId}/profile")
    public ResponseEntity<MemberProfileDto> getMyProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long memberId
    ) {
        //비로그인이라면(=null이라면) , 0 또는 null 전달하도록
        Long loginMemberId = (userDetails!=null) ? userDetails.getMemberId() : null;

        MemberProfileDto dto = memberService.getMemberProfile(
            memberId,                   // 셀러샵 회원
//            userDetails.getMemberId()   // 로그인 회원 (좋아요 확인용)
            loginMemberId              //현 로그인 사용자id(비로그인시 null)
        );
        return ResponseEntity.ok(dto);
    }

    // 회원정보 설정 페이지 정보 조회
    @Operation(summary = "회원정보 설정 페이지 조회", description = "회원정보 설정 페이지 멤버 조회")
    @GetMapping("me/edit")
    public ResponseEntity<MemberEditResponse> getMemberEdit(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long memberId = userPrincipal.getMemberId();
        MemberEditResponse dto = memberService.getMemberEdit(memberId);

        return ResponseEntity.ok(dto);
    }

    // 회원정보 설정 페이지 정보 수정
    @Operation(summary = "회원정보 설정 페이지 수정", description = "핸드폰 번호, 이메일 수정")
    @PatchMapping("me/edit")
    public ResponseEntity<MemberEditResponse> updateMemberEdit(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @RequestBody MemberEditRequest request
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long memberId = userPrincipal.getMemberId();
        MemberEditResponse dto = memberService.updateMemberEdit(memberId, request);

        return ResponseEntity.ok(dto);
    }
}
