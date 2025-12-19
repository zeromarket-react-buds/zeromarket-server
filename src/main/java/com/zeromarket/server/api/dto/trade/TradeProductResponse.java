package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TradeProductResponse {
    private Long memberId;
    private Long tradeId;
    private Long productId;

    private String productTitle;
    private String thumbnailUrl;
    private Integer sellPrice;

    @Pattern(regexp = "SELLER|BUYER")
    private String canceledBy;

    private Long sellerId;
    private String nickname;

    private Long buyerId;
    private String name;
    private String phone;

    private TradeType tradeType;
    private TradeStatus tradeStatus;

    private LocalDateTime createdAt;     // 거래 생성 & 결제 시간
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;   // 거래 완료
    private LocalDateTime canceledAt;    // 거래 취소

    private TradeReviewStatusResponse reviewStatus;
}
