package com.zeromarket.server.api.dto.order;

import com.zeromarket.server.common.enums.PaymentMethod;
import com.zeromarket.server.common.enums.TradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    /* Trade 생성용 */
    @NotNull
    private Long productId;

    @NotNull
    private Long buyerId;

    @NotNull
    private TradeType tradeType;
    // DIRECT / DELIVERY


    /* Order 생성용 */
    @Positive
    private BigDecimal amountPaid;

//    @NotNull
//    private PaymentType paymentType;
//    // DIRECT / NON_DIRECT

    @NotNull
    private PaymentMethod paymentMethod;
    // CASH / INTERNAL

    /* 배송지 스냅샷*/
    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String addrBase;

    @NotBlank
    private String addrDetail;
}

