package com.zeromarket.server.api.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeHistoryRequest {
    private Long memberId;
    private String keyword;
}
