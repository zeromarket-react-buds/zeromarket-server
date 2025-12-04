package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.*;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;

import java.util.List;

public interface TradeHistoryService {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest tradeHistoryRequest);

    TradeProductResponse selectTradeProduct(TradeProductRequest tradeProductRequest);

    TradeReviewInfoDto getTradeInfoForReview(Long tradeId, Long loginMemberId);

    TradeStatusUpdateResponse updateTradeStatus(Long tradeId, TradeStatusUpdateRequest request, Long memberId);
}
