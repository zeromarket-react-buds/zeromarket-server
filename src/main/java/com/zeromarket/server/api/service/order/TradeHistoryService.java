package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.TradeHistoryRequest;
import com.zeromarket.server.api.dto.order.TradeHistoryResponse;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.TradeProductRequest;
import com.zeromarket.server.api.dto.order.TradeProductResponse;

import java.util.List;

public interface TradeHistoryService {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest tradeHistoryRequest);

    TradeProductResponse selectTradeProduct(TradeProductRequest tradeProductRequest);

    TradeReviewInfoDto getTradeInfoForReview(Long tradeId, Long loginMemberId);
}
