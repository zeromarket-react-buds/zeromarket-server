package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeStatusUpdateRow {

    private Long tradeId;
    private Long sellerId;
    private Long buyerId;
    private TradeStatus tradeStatus;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

}
