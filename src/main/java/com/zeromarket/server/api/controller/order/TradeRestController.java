package com.zeromarket.server.api.controller.order;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.*;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.order.TradeHistoryService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trades")
@Tag(name = "거래 API", description = "거래 관련 API")
public class TradeRestController {

    private TradeHistoryService tradeHistoryService;

    @Operation(summary = "판매자에 의한 예약 중 상태의 거래 내역 생성", description = "판매자에 의한 예약 중 상태의 거래 내역 생성")
    @PostMapping("/pending")
    public ResponseEntity<Long> processTradePendingBySeller(
        @RequestBody TradeRequest tradeRequest,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long tradeId =
            tradeHistoryService.processTradePendingBySeller(tradeRequest, userPrincipal.getMemberId());
        return ResponseEntity.ok(tradeId);
    }

    @Operation(summary = "판매자에 의한 거래 완료 상태의 거래 내역 생성 혹은 상태 변경", description = "판매자에 의한 거래 완료 상태의 거래 내역 생성 혹은 상태 변경")
    @PostMapping("/complete")
    public ResponseEntity<Long> processTradeCompleteBySeller(
        @RequestBody TradeRequest tradeRequest,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long tradeId =
            tradeHistoryService.processTradeCompleteBySeller(tradeRequest, userPrincipal.getMemberId());
        return ResponseEntity.ok(tradeId);
    }

    @Operation(summary = "거래 목록 조회", description = "검색 포함 거래 목록 조회")
    @GetMapping
    public ResponseEntity<List<TradeHistoryResponse>> getTradeList(
        @ModelAttribute TradeHistoryRequest tradeHistoryRequest,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        tradeHistoryRequest.setMemberId(userPrincipal.getMemberId());

        List<TradeHistoryResponse> result =
            tradeHistoryService.selectTradeList(tradeHistoryRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "거래 내역 상세 조회", description = "거래내역 페이지 조회")
    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeProductResponse> getTradeProduct(
        @PathVariable Long tradeId,
        @ModelAttribute TradeProductRequest tradeProductRequest,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        tradeProductRequest.setTradeId(tradeId);
        tradeProductRequest.setMemberId(userPrincipal.getMemberId());

        TradeProductResponse result =
            tradeHistoryService.selectTradeProduct(tradeProductRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "거래완료/취소로 거래상태 변경", description = "현재 거래상태를 거래완료(COMPLETED)/취소(CANCELED)로 변경")
    @PatchMapping("/{tradeId}/status")
    public ResponseEntity<TradeStatusUpdateResponse> updateTradeStatus(
        @PathVariable Long tradeId,
        @RequestBody TradeStatusUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long memberId = userPrincipal.getMemberId();

        TradeStatusUpdateResponse response =
            tradeHistoryService.updateTradeStatus(tradeId, request, memberId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "거래내역을 소프트 딜리트", description = "현재 거래상태를 seller_deleted/buyer_deleted로 해서 목록에서 안보이게 변경")
    @PatchMapping("/{tradeId}/delete")
    public ResponseEntity<Void> softDeleteTrade(
        @PathVariable Long tradeId,
        @RequestBody TradeSoftDeleteRequest request,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        Long memberId = userPrincipal.getMemberId();

        tradeHistoryService.softDeleteTrade(tradeId, request.getDeletedBy(), memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "후기용 거래 상세 조회", description = "")
    @GetMapping("reviews/{tradeId}")
    public ResponseEntity<TradeReviewInfoDto> getTrade(
        @PathVariable Long tradeId,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        TradeReviewInfoDto dto = tradeHistoryService.getTradeInfoForReview(tradeId, userPrincipal.getMemberId());
        return ResponseEntity.ok(dto);
    }
}
