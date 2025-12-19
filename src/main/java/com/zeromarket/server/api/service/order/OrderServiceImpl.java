package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.CreateOrderRequest;
import com.zeromarket.server.api.dto.order.OrderCompleteDto;
import com.zeromarket.server.api.dto.order.TradeRequest;
import com.zeromarket.server.api.mapper.order.OrderMapper;
import com.zeromarket.server.api.mapper.trade.TradeHistoryMapper;
import com.zeromarket.server.common.entity.Order;
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

//todo: 거래 중복 발생 가능
//todo: 상태 전이 유효성 검사

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

    /**
     * 결제 완료 후 거래 내역 조회
     * @param orderId
     * @param memberId
     * @return
     */
    public OrderCompleteDto selectOrderComplete(Long orderId, Long memberId) {
        if(memberId == null){throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        OrderCompleteDto dto = orderMapper.selectOrderComplete(orderId, memberId);

        if (dto == null) {
            throw new ApiException(ErrorCode.TRADE_NOT_FOUND); // "주문 정보를 찾을 수 없습니다."
        }

        return dto;
    };

    /**
     * 배송 시작 (order.DELIVERY_READY)
     * @param orderId
     */
    @Transactional
    public void markDeliveryReady(Long orderId) {
        orderMapper.updateOrderStatus(orderId, OrderStatus.DELIVERY_READY.name());
    }

    /**
     * 배송 중 (order.SHIPPED)
     * @param orderId
     */
    @Transactional
    public void ship(Long orderId) {
        orderMapper.updateOrderStatus(orderId, OrderStatus.SHIPPED.name());
    }

    /**
     * 배송 완료 -> 거래 완료 (order.DELIVERED, trade.COMPLETED)
     * @param orderId
     * @param tradeId
     */
    @Transactional
    public void completeOrder(Long orderId, Long tradeId) {

        orderMapper.updateOrderStatus(orderId, OrderStatus.DELIVERED.name());
//        tradeMapper.updateTradeStatus(tradeId, TradeStatus.COMPLETED.name());
    }

    /**
     * 주문 취소 (order.CANCELED, trade.CANCELED)
     * @param orderId
     * @param tradeId
     */
    @Transactional
    public void cancelOrder(Long orderId, Long tradeId) {

        orderMapper.updateOrderStatus(orderId, OrderStatus.CANCELED.name());
//        tradeMapper.updateTradeStatus(tradeId, TradeStatus.CANCELED);
    }
}
