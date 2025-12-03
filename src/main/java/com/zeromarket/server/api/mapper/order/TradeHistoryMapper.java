package com.zeromarket.server.api.mapper.order;

import com.zeromarket.server.api.dto.order.TradeHistoryRequest;
import com.zeromarket.server.api.dto.order.TradeHistoryResponse;
import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.TradeProductRequest;
import com.zeromarket.server.api.dto.order.TradeProductResponse;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeHistoryMapper {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req);

    TradeProductResponse selectTradeProduct(TradeProductRequest req);

    TradeReviewInfoDto selectTradeInfoForReview(
        @Param("tradeId") Long tradeId,
        @Param("loginMemberId") Long loginMemberId
    );

    Map<String, Long> selectSellerBuyerByTradeId(Long tradeId);
}
