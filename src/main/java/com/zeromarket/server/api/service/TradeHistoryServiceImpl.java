package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.*;
import com.zeromarket.server.api.mapper.TradeHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TradeHistoryServiceImpl implements TradeHistoryService{
    private final TradeHistoryMapper mapper;

    @Override
    public List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req) {

        TradeHistoryRequest tradeReq = new TradeHistoryRequest();
        tradeReq.setKeyword(req.getKeyword());

        // 거래 목록 조회
        List<TradeHistoryResponse> histories =
            mapper.selectTradeList(req);

        return histories;
    }
}
