package com.zeromarket.server.api.dto.mypage;

import lombok.Data;

@Data
public class TradeReviewInfoDto {
    private Long tradeId;
    private Long productId;
    private String productTitle;
    private String productImageUrl;
    private String opponentNickname;
}
