package com.zeromarket.server.api.dto.order;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeProductRequest {
    private Long tradeId;
    private Long memberId;

    @Pattern(regexp = "SELLER|BUYER")
    private String canceledBy;
}
