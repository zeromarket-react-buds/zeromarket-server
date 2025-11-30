package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.TradeHistoryRequest;
import com.zeromarket.server.api.dto.TradeHistoryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TradeHistoryMapper {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req);
}
