package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeStatusUpdateRequest {
    private TradeStatus status;
}
