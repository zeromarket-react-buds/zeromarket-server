package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeStatusUpdateRow {

    private Long tradeId;
    private Long productId;
    private Long sellerId;
    private Long buyerId;

    private TradeStatus tradeStatus;
    private TradeType tradeType;
    private Long orderId;
    private OrderStatus orderStatus;

    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;
    private String canceledBy; // SELLER / BUYER

    private Boolean sellerDeleted; // 판매자 삭제 여부
    private Boolean buyerDeleted; //구매자 삭제 여부
}
