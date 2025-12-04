package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeHistoryResponse {

    private Long productId;
    private Long tradeId;
    private Long buyerId;

    private String productTitle;
    private String thumbnailUrl;

    private Integer sellPrice;

    private TradeType tradeType;
    private TradeStatus tradeStatus;

    private Boolean isDirect;
    private Boolean isDelivery;

    private Boolean isHidden;

    private Boolean sellerDeleted;
    private Boolean buyerDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;

    private TradeReviewStatusResponse reviewStatus;

}
