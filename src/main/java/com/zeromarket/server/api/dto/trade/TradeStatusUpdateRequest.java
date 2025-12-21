package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeStatusUpdateRequest {
    private TradeStatus status;     // 거래상태 COMPLETED/CANCELED
    private OrderStatus orderStatus; // 주문상태 (바로구매 진행용)
}
