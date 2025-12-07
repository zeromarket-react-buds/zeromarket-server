package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeStatusUpdateResponse {

    private Long tradeId;
    private TradeStatus tradeStatus;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;
    private String canceledBy; // SELLER / BUYER

}
