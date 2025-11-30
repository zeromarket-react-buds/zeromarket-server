package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.*;
import com.zeromarket.server.api.service.TradeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
}
