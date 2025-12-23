package com.zeromarket.server.api.controller.noti;

import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.noti.KeywordAlertService;
import com.zeromarket.server.common.entity.KeywordAlert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keywords")
@SecurityRequirement(name = "bearer")
public class KeywordAlertRestController {

    private final KeywordAlertService keywordAlertService;

    @Operation(
        summary = "등록한 키워드 목록 조회",
        description = "사용자가 등록한 알림용 키워드 목록을 조회합니다"
    )
    @GetMapping
    public ResponseEntity<List<KeywordAlert>> selectKeywordAlerts(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        List<KeywordAlert> keywordAlerts = keywordAlertService.selectKeywordAlertsByMember(
            memberId);
        return ResponseEntity.ok(keywordAlerts);
    }

    @Operation(
        summary = "키워드알림ID로 키워드 조회",
        description = "키워드알림ID로 키워드 설정을 조회합니다"
    )
    @GetMapping("/{alertId}")
    public ResponseEntity<KeywordAlert> getKeywordAlertsByIds(
        @PathVariable("alertId") Long alertId,
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        KeywordAlert keywordAlert = keywordAlertService.getKeywordAlertsByIds(alertId,
            memberId);
        return ResponseEntity.ok(keywordAlert);
    }

    @Operation(
        summary = "알림용 키워드 등록",
        description = "알림용 키워드를 등록합니다."
    )
    @PostMapping
    public ResponseEntity<List<KeywordAlert>> insertKeywordAlert(
        KeywordAlert keywordAlert,
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        keywordAlert.setMemberId(memberId);
        keywordAlertService.insertKeywordAlert(keywordAlert);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "알림용 키워드 수정",
        description = "알림용 키워드를 수정합니다."
    )
    @PutMapping
    public ResponseEntity<List<KeywordAlert>> updateKeywordAlert(
        KeywordAlert keywordAlert,
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        keywordAlert.setMemberId(memberId);
        keywordAlertService.updateKeywordAlert(keywordAlert);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "알림용 키워드 삭제",
        description = "알림용 키워드를 삭제합니다."
    )
    @DeleteMapping("/{alertId}")
    public ResponseEntity<List<KeywordAlert>> deleteKeywordAlert(
        @PathVariable Long alertId,
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        keywordAlertService.deleteKeywordAlert(alertId, memberId);
        return ResponseEntity.ok().build();
    }

}
