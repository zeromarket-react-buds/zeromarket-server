package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.CreateOrderRequest;
import com.zeromarket.server.api.dto.order.TradeRequest;
import com.zeromarket.server.api.mapper.order.OrderMapper;
import com.zeromarket.server.api.mapper.order.TradeHistoryMapper;
import com.zeromarket.server.common.entity.Order;
import com.zeromarket.server.common.entity.Trade;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상태 전이 규칙
 * [order 생성]
 * -> order.PAID
 * -> trade.PENDING
 *
 * [배송 완료]
 * -> order.DELIVERED
 * -> trade.CANCELED
 *
 * [취소]
 * -> order.CANCELED
 * -> trade.CANCELED
 */

@RequiredArgsConstructor
@Service
public class OrderServiceImpl {
    private final TradeHistoryMapper tradeMapper;
    private final OrderMapper orderMapper;

    /**
     * 주문 생성 트랜잭션
     * @param request
     * @return
     */
    @Transactional
    public Long createOrder(CreateOrderRequest request, Long memberId) {

        if(memberId == null){throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        // 1️⃣ trade 생성 (거래 관계)
        TradeRequest tradeRequest = TradeRequest.builder()
            .buyerId(memberId)
            .productId(request.getProductId())
            .tradeType(request.getTradeType())   // DIRECT / DELIVERY
            .tradeStatus(TradeStatus.PENDING)    // Trade.PENDING
            .build();

        tradeMapper.createTrade(tradeRequest);

        // 2️⃣ order 생성 (결제 완료 상태)
        Order order = Order.builder()
            .orderStatus(OrderStatus.PAID.name())          // Order.PAID
            .tradeId(tradeRequest.getTradeId())
            .buyerId(memberId)
            .amountPaid(request.getAmountPaid())
            .paymentMethod(request.getPaymentMethod().name())
            .receiverName(request.getReceiverName())
            .receiverPhone(request.getReceiverPhone())
            .deliveryZipcode(request.getZipcode())
            .deliveryAddrBase(request.getAddrBase())
            .deliveryAddrDetail(request.getAddrDetail())
            .build();

        orderMapper.insertOrder(order);

        return order.getOrderId();
    }

//    /**
//     * 배송 시작
//     * @param orderId
//     */
//    @Transactional
//    public void markDeliveryReady(Long orderId) {
//        orderMapper.updateOrderStatus(orderId, OrderStatus.DELIVERY_READY);
//    }
//
//    /**
//     * 배송 중
//     * @param orderId
//     */
//    @Transactional
//    public void ship(Long orderId) {
//        orderMapper.updateOrderStatus(orderId, OrderStatus.SHIPPED);
//    }
//
//    /**
//     * 배송 완료 -> 거래 완료
//     * @param orderId
//     * @param tradeId
//     */
//    @Transactional
//    public void completeOrder(Long orderId, Long tradeId) {
//
//        orderMapper.updateOrderStatus(orderId, OrderStatus.DELIVERED);
//        tradeMapper.updateTradeStatus(tradeId, TradeStatus.COMPLETED);
//    }
//
//    /**
//     * 주문 취소
//     * @param orderId
//     * @param tradeId
//     */
//    @Transactional
//    public void cancelOrder(Long orderId, Long tradeId) {
//
//        orderMapper.updateOrderStatus(orderId, OrderStatus.CANCELED);
//        tradeMapper.updateTradeStatus(tradeId, TradeStatus.CANCELED);
//    }
}
