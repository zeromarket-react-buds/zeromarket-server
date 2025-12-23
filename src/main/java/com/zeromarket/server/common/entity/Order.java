package com.zeromarket.server.common.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Order {
    private Long orderId;
    private Long tradeId;
    private Long buyerId;
    private String name;
    private String orderStatus;     // PAID / PREPARING / SHIPPED / DELIVERED / CANCELED

    private BigDecimal amountPaid;
    private String paymentMethod;   // CASH / INTERNAL

    private String receiverName;
    private String receiverPhone;
    private String deliveryZipcode;
    private String deliveryAddrBase;
    private String deliveryAddrDetail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

