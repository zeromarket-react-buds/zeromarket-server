package com.zeromarket.server.api.controller.order;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.TradeHistoryRequest;
import com.zeromarket.server.api.dto.order.TradeHistoryResponse;
import com.zeromarket.server.api.dto.order.TradeProductRequest;
import com.zeromarket.server.api.dto.order.TradeProductResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.order.TradeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trades")
@Tag(name = "거래 API", description = "거래 관련 API")
public class TradeRestController {

    private TradeHistoryService tradeHistoryService;

    @Operation(summary = "거래 목록 조회", description = "검색 포함 거래 목록 조회")
    @GetMapping
    public ResponseEntity<List<TradeHistoryResponse>> getTradeList(
        @ModelAttribute TradeHistoryRequest tradeHistoryRequest
    ) {
        List<TradeHistoryResponse> result = tradeHistoryService.selectTradeList(tradeHistoryRequest);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "거래 내역 상세 조회", description = "거래내역 페이지 조회")
    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeProductResponse> getTradeProduct(
        @PathVariable Long tradeId,
        @ModelAttribute TradeProductRequest tradeProductRequest
    ) {
        tradeProductRequest.setTradeId(tradeId);
        TradeProductResponse result = tradeHistoryService.selectTradeProduct(tradeProductRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "후기용 거래 상세 조회", description = "")
    @GetMapping("{tradeId}/reviews")
    public ResponseEntity<TradeReviewInfoDto> getTrade(
        @PathVariable Long tradeId,
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        TradeReviewInfoDto dto = tradeHistoryService.getTradeInfoForReview(tradeId, userPrincipal.getMemberId());
        return ResponseEntity.ok(dto);
    }
}
