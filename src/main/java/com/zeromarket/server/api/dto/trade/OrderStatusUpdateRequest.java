package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
