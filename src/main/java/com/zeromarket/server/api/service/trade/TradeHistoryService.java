package com.zeromarket.server.api.service.trade;

import com.zeromarket.server.api.dto.order.*;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.trade.*;

import java.util.List;

public interface TradeHistoryService {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest tradeHistoryRequest);

    TradeProductResponse selectTradeProduct(TradeProductRequest tradeProductRequest);

    TradeReviewInfoDto getTradeInfoForReview(Long tradeId, Long loginMemberId);

    TradeStatusUpdateResponse updateTradeStatus(Long tradeId, TradeStatusUpdateRequest request, Long memberId);

    Long processTradePendingBySeller(TradeRequest tradeRequest, Long memberId);
    Long processTradeCompleteBySeller(TradeRequest tradeRequest, Long memberId);

    void softDeleteTrade(Long tradeId, String deletedBy, Long memberId);
}
