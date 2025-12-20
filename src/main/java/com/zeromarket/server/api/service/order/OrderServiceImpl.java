package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.order.CreateOrderRequest;
import com.zeromarket.server.api.dto.order.OrderCompleteDto;
import com.zeromarket.server.api.dto.order.TradeRequest;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.mapper.mypage.ProductMapper;
import com.zeromarket.server.api.mapper.order.OrderMapper;
import com.zeromarket.server.api.mapper.product.ProductCommandMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.api.mapper.trade.TradeHistoryMapper;
import com.zeromarket.server.common.entity.Order;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.SalesStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import com.zeromarket.server.common.exception.ApiException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상태 전이 규칙
 * [결제 완료]
 * -> order.PAID
 * -> trade.PENDING
 * -> product.RESERVED
 *
 * [배송 완료]
 * -> order.DELIVERED
 * -> trade.COMPLETED
 * -> product.SOLD_OUT
 *
 * [취소]
 * -> order.CANCELED
 * -> trade.CANCELED
 * -> product.FOR_SALE
 */

//todo: 거래 중복 발생 가능
//todo: 상태 전이 유효성 검사

@RequiredArgsConstructor
@Service
public class OrderServiceImpl {
    private final TradeHistoryMapper tradeMapper;
    private final OrderMapper orderMapper;
    private final ProductQueryMapper productQueryMapper;
    private final ProductCommandMapper productCommandMapper;

    /**
     * 주문 생성 트랜잭션
     * @param request
     * @return
     */
    @Transactional
    public Long createOrder(CreateOrderRequest request, Long memberId) {

        // 1. 회원 검증
        if(memberId == null){throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);}

        // 2. 상품 상태 검증 (이미 팔린 상품 결제 방지)
        // - 상품 존재 여부 / 판매 중 상태인지(FOR_SALE) / 삭제&숨김 여부(is_deleted, is_hidden)
        // - 중복 거래 문제 (동시성 문제) 'FOR UPDATE' 구문 추가 (위치: mapper.xml)
        ProductDetailResponse product = productQueryMapper.selectProductForOrder(request.getProductId());

        if (product == null || !product.getSalesStatus().equals(SalesStatus.FOR_SALE)) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOR_SALE);
        }

        // 3. 자기 상품 구매 금지
        if (product.getSellerId().equals(memberId)) {
            throw new ApiException(ErrorCode.CANNOT_BUY_OWN_PRODUCT);
        }

        // 4. 중복 결제 방지 (보조 수단)
        // - order.paid/delivery_ready/shipped
        boolean exists = tradeMapper.existsActiveOrderByProductId(
            request.getProductId()
        );

        if (exists) {
            throw new ApiException(ErrorCode.TRADE_ALREADY_EXIST);
        }

        // 5. 결제 금액 위변조 방지 (거래 금액, 프론트 조작 금지)
        if (request.getAmountPaid().compareTo(BigDecimal.valueOf(product.getSellPrice())) != 0) {
            throw new ApiException(ErrorCode.TRADE_INVALID_PAYMENT_AMOUNT);
        }

        // 6. 거래 유형별 검사
        if (request.getTradeType() == TradeType.DELIVERY) {

            if (request.getReceiverName() == null ||
                request.getReceiverPhone() == null ||
                request.getZipcode() == null ||
                request.getAddrBase() == null) {

                throw new ApiException(ErrorCode.DELIVERY_INFO_REQUIRED);
            }
        }

        // 7. trade 생성 (거래 관계)
        TradeRequest tradeRequest = TradeRequest.builder()
            .buyerId(memberId)
            .productId(request.getProductId())
            .tradeType(request.getTradeType())   // DIRECT / DELIVERY
            .tradeStatus(TradeStatus.PENDING)    // Trade.PENDING
            .build();

        tradeMapper.createTrade(tradeRequest);

        // 8. order 생성 (결제 완료 상태)
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

        // 9. 상품 상태 변경
        productCommandMapper.updateProductStatus(product.getProductId(), SalesStatus.RESERVED);

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
