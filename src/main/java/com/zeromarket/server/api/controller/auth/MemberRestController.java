package com.zeromarket.server.api.controller.auth;

import com.zeromarket.server.api.dto.auth.KakaoLinkRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.WithdrawRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MemberRestController
 * - getMyInfo          /api/members/me
 * - logout             /api/members/logout
 * - withdraw           /api/members/withdraw
 * - linkKakao          /api/members/oauth/kakao/link
 * - unlinkKakao        /api/members/oauth/kakao/unlink
 */

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "Member endpoints")
public class MemberRestController {

    private final AuthService authService;
    private final MemberService memberService;
    private final OAuthService oAuthService;

    // 인증 관련제어 (인증 정보 조회 API)
    @Operation(summary = "My info", description = "Fetch current member info from auth context")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        MemberResponse response = memberService.getMyInfo(userDetails.getLoginId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout", description = "Invalidate session/token")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Withdraw", description = "Deactivate member account")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody(required = false) WithdrawRequest request,
        HttpServletResponse response
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        memberService.withdraw(userDetails.getMemberId(), request, response);
        return ResponseEntity.ok().build();
    }

    // 타 회원정보 조회 (프로필 적용, 셀러샵 사용)
    @Operation(summary = "Profile lookup", description = "Public profile of a member")
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

    @Operation(summary = "카카오 계정 연동", description = "로그인 후 카카오 계정을 현재 계정에 연결")
    @PostMapping("/oauth/kakao/link")
    public ResponseEntity<?> linkKakao(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody KakaoLinkRequest request
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        oAuthService.linkKakao(request.getCode(), request.getRedirectUri(), userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카카오 계정 연동 해제", description = "로그인 후 카카오 연동을 해제")
    @PostMapping("/oauth/kakao/unlink")
    public ResponseEntity<?> unlinkKakao(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        oAuthService.unlinkKakao(userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}
