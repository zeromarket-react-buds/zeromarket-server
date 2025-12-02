package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.TradeHistoryRequest;
import com.zeromarket.server.api.dto.TradeHistoryResponse;
import com.zeromarket.server.api.dto.TradeReviewInfoDto;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeHistoryMapper {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req);

    TradeReviewInfoDto selectTradeInfoForReview(
        @Param("tradeId") Long tradeId,
        @Param("loginMemberId") Long loginMemberId
    );


}
