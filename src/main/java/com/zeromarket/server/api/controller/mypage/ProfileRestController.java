package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileEditResponse;
import com.zeromarket.server.api.dto.mypage.ProfileSettingRequest;
import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import com.zeromarket.server.api.dto.mypage.NicknameCheckResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.mypage.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/profile")
@RequiredArgsConstructor
@Tag(name = "회원 프로필 수정 관련 API", description = "프로필 설정 페이지 / 회원정보 설정 페이지 관련 API")
public class ProfileRestController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 설정 조회", description = "프로필 설정페이지 멤버 조회")
    @GetMapping
    public ProfileSettingResponse getProfileSetting(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        Long memberId = userPrincipal.getMemberId();
        return profileService.selectProfileSetting(memberId);
    }

    @Operation(summary = "프로필 설정 수정", description = "프로필 이미지, 닉네임, 한줄소개 수정")
    @PatchMapping
    public ProfileSettingResponse updateProfileSetting(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @RequestBody ProfileSettingRequest request
    ) {
        Long memberId = userPrincipal.getMemberId();
        return profileService.updateProfileSetting(memberId, request);
    }

    @GetMapping("nickname/check")
    public NicknameCheckResponse checkNickname(
        @RequestParam String nickname,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        Long memberId = userPrincipal.getMemberId();

        boolean exists = profileService.existsByNicknameExcludingMe(nickname, memberId);

        NicknameCheckResponse response = new NicknameCheckResponse();
        response.setExists(exists);
        return response;
    }

    @Operation(summary = "회원정보 설정 페이지 조회", description = "회원정보 설정 페이지 멤버 조회")
    @GetMapping("edit")
    public ProfileEditResponse getProfileEdit(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        Long memberId = userPrincipal.getMemberId();
        return profileService.selectProfileEdit(memberId);
    }
}