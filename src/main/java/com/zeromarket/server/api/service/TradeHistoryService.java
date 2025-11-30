package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.TradeHistoryRequest;
import com.zeromarket.server.api.dto.TradeHistoryResponse;

import java.util.List;

public interface TradeHistoryService {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest tradeHistoryRequest);
}
