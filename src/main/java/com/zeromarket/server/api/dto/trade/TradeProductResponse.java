package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.OrderStatus;
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
    private Long orderId;

    private String productTitle;
    private String thumbnailUrl;
    private Integer sellPrice;

    @Pattern(regexp = "SELLER|BUYER")
    private String canceledBy;

    private Long sellerId;
    private String sellerNickname;
    private String buyerNickname;

    private Long buyerId;
    private String name;
    private String phone;
    private String zipcode;
    private String addrBase;
    private String addrDetail;

    private TradeType tradeType;
    private TradeStatus tradeStatus;
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;     // 거래 생성 & 결제 시간 (trade 테이블 createdAt)
    private LocalDateTime updatedAt;     // trade 테이블 updatedAt
    private LocalDateTime orderUpdatedAt; // order 테이블의 updatedAt값
    private LocalDateTime completedAt;   // 거래 완료
    private LocalDateTime canceledAt;    // 거래 취소

    private TradeReviewStatusResponse reviewStatus;
}
