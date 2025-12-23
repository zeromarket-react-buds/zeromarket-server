package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import com.zeromarket.server.common.enums.UserType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeRequest {

    private Long tradeId;
    private Long productId;
    private Long sellerId;
    private Long buyerId;
    private TradeType tradeType;
    private TradeStatus tradeStatus;
    private UserType canceledBy;
    private boolean sellerDeleted;
    private boolean buyerDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;
}
