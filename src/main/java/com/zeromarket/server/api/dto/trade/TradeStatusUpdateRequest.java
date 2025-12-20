package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeStatusUpdateRequest {
    private TradeStatus status;     // COMPLETED / CANCELED (채팅/거래 종료용)
    private OrderStatus orderStatus; // PAID/DELIVERY_READY/SHIPPED/DELIVERED/CANCELED (바로구매 진행용)
}
