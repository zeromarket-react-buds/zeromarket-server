package com.zeromarket.server.api.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class OrderCompleteDto {

    private Long orderId;
    private String tradeType;
    private LocalDateTime createdAt;

    // 상품
    private String productTitle;
    private BigDecimal productPrice;
    private String productImageUrl;

    // 배송 (DELIVERY일 때만 의미 있음)
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;

    // 결제
    private String paymentMethod;
    private BigDecimal amountPaid;
}

