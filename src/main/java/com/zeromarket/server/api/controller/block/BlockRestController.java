package com.zeromarket.server.api.controller.block;

import com.zeromarket.server.api.dto.block.BlockListResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.block.BlockService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/block")
@Tag(name = "차단 API", description = "차단 관련 API")
public class BlockRestController {

    private BlockService blockService;

    @Operation(summary = "차단 목록 조회", description = "차단 유저 목록 페이지 리스트 조회")
    @GetMapping("/list")
    public ResponseEntity<BlockListResponse> getBlockList(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) throw new ApiException(ErrorCode.FORBIDDEN);

        Long memberId = userPrincipal.getMemberId();
        BlockListResponse res = blockService.getBlockList(memberId);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "차단 해제", description = "차단 유저 목록 페이지에서 해당 유저 차단해제")
    @PatchMapping("/{blockId}")
    public ResponseEntity<Void> unblock(
        @PathVariable Long blockId,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        blockService.updateUnblock(blockId, userPrincipal.getMemberId());
        return ResponseEntity.ok().build();
    }
}
