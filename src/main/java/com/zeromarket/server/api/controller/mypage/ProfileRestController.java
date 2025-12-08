package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
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
@Tag(name = "마이페이지 API", description = "마이페이지 관련 API")
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
}