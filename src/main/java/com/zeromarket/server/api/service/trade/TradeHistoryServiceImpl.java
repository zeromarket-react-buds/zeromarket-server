package com.zeromarket.server.api.service.trade;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.*;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.dto.trade.*;
import com.zeromarket.server.api.mapper.mypage.ReviewMapper;
import com.zeromarket.server.api.mapper.trade.TradeHistoryMapper;
import com.zeromarket.server.api.mapper.product.ProductCommandMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.common.entity.Review;
import com.zeromarket.server.common.entity.Trade;
import com.zeromarket.server.common.enums.*;
import com.zeromarket.server.common.exception.ApiException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.CollectionUtils;

@Service
@AllArgsConstructor
public class TradeHistoryServiceImpl implements TradeHistoryService {

    private final TradeHistoryMapper mapper;
    private final ReviewMapper reviewMapper;
    private final ProductQueryMapper productQueryMapper;
    private final ProductCommandMapper productCommandMapper;

    // 거래 목록 조회
    @Override
    public List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req) {

        // 컨트롤러에서 세팅해준 로그인 회원 id
        Long loginMemberId = req.getMemberId();

        // 상태 / 숨기기 필터 가공
        applyStatusFilter(req);

        // id랑 role에 의하 검색/필터용
        List<TradeHistoryResponse> histories = mapper.selectTradeList(req);

        // 각 거래별로 리뷰 상태 계산해서 붙이기
        for (TradeHistoryResponse history : histories) {
            TradeReviewStatusResponse status = buildReviewStatus(
                history.getTradeId(),
                history.getTradeStatus(),
                loginMemberId
            );
            history.setReviewStatus(status);
        }

        return histories;
    }

    // 상태/숨기기 필터 가공 메서드
    private void applyStatusFilter(TradeHistoryRequest req) {

        List<String> status = req.getStatus();

        if (status != null && !status.isEmpty()) {

            // 문자열 상태 리스트를 TradeStatus enum 리스트로 변환 (매칭 안 되는 값은 무시)
            List<TradeStatus> tradeStatus =
                status.stream()
                    .map(s -> Arrays.stream(TradeStatus.values())
                        .filter(ts -> ts.name().equals(s))
                        .findFirst()
                        .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            // isHidden은 별도 Boolean으로 처리
            boolean hidden = status.contains("isHidden");

            // 가공된 값들을 Request에 반영
            req.setTradeStatus(tradeStatus.isEmpty() ? null : tradeStatus);
            req.setIsHidden(hidden);

        } else {
            req.setTradeStatus(null);
            req.setIsHidden(null);
        }
    }

    // 리뷰 상태 공통 계산 메서드
    private TradeReviewStatusResponse buildReviewStatus(
        Long tradeId,
        TradeStatus tradeStatus,
        Long memberId
    ) {
        // 로그인 안 했으면 리뷰 상태 붙일 수 없음
        if (memberId == null) {
            return null;
        }

        // 해당 거래에 달린 리뷰들
        List<Review> reviews = reviewMapper.selectReviewsByTradeId(tradeId);

        Review my = null;
        Review partner = null;

        for (Review r : reviews) {
            if (r.getWriterId().equals(memberId)) {
                my = r;
            } else {
                partner = r;
            }
        }

        TradeReviewStatusResponse status = new TradeReviewStatusResponse();
        status.setMyReviewExists(my != null);
        status.setPartnerReviewExists(partner != null);

        status.setMyReviewId(my != null ? my.getReviewId() : null);
        status.setPartnerReviewId(partner != null ? partner.getReviewId() : null);

        boolean isCompleted = tradeStatus == TradeStatus.COMPLETED;

        status.setCanWriteReview(isCompleted && my == null);

        return status;
    }

    // 거래 상세 내역 조회
    @Override
    public TradeProductResponse selectTradeProduct(TradeProductRequest req) {

        TradeProductResponse product = mapper.selectTradeProduct(req);

        if (product == null) {
            throw new ApiException(ErrorCode.TRADE_NOT_FOUND);
        }

        Long tradeId = product.getTradeId();
        Long memberId = req.getMemberId();

        product.setMemberId(memberId);

        TradeReviewStatusResponse status = buildReviewStatus(
            tradeId,
            product.getTradeStatus(),
            memberId
        );
        product.setReviewStatus(status);

        return product;
    }

    /* 거래 상태 업데이트
        - 취소 가능 조건:
            1) 채팅거래(CHAT_DIRECT, order 없음) -> trade_status == PENDING 일 때만
            2) 바로구매(INSTANT_*, order 있음)  -> order_status == PAID 일 때만
        - 완료 가능 조건(판매자만):
            1) 채팅거래 -> trade_status == PENDING
            2) 바로구매 DIRECT -> order_status == DELIVERY_READY (완료 시 DELIVERED로 맞춤)
            3) 바로구매 DELIVERY -> order_status == DELIVERED (이미 배송완료일 때만 완료 가능) */

    @Override
    @Transactional
    public TradeStatusUpdateResponse updateTradeStatus(
        Long tradeId,
        TradeStatusUpdateRequest request,
        Long memberId
    ) {
        TradeStatusUpdateRow trade = mapper.selectById(tradeId);
        if (trade == null) {
            throw new IllegalArgumentException("존재하지 않는 거래입니다.");
        }

        boolean isSeller = memberId != null && memberId.equals(trade.getSellerId());
        boolean isBuyer = memberId != null && memberId.equals(trade.getBuyerId());
        if (!isSeller && !isBuyer) {
            throw new IllegalStateException("해당 거래의 당사자가 아닙니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 취소/완료는 정합성 때문에 일괄 상태 변경 처리
        boolean cancelRequested =
            (request.getStatus() == TradeStatus.CANCELED) ||
                (request.getOrderStatus() == OrderStatus.CANCELED);

        boolean completeRequested =
            (request.getStatus() == TradeStatus.COMPLETED);

        if (cancelRequested) {
            cancelTradeAndOrder(trade, tradeId, memberId, now);

            TradeStatusUpdateResponse response = new TradeStatusUpdateResponse();
            response.setTradeId(tradeId);
            response.setTradeStatus(TradeStatus.CANCELED);
            response.setUpdatedAt(now);
            return response;
        }

        if (completeRequested) {
            // 완료는 판매자만 허용
            if (!isSeller) {
                throw new IllegalStateException("거래 완료는 판매자만 처리할 수 있습니다.");
            }

            completeTradeAndOrderIfNeeded(trade, tradeId, now);

            TradeStatusUpdateResponse response = new TradeStatusUpdateResponse();
            response.setTradeId(tradeId);
            response.setTradeStatus(TradeStatus.COMPLETED);
            response.setUpdatedAt(now);
            return response;
        }

        // 그 외는 주문 상태만 변경만 허용 (예: 주문확인 버튼)
        if (request.getOrderStatus() != null) {
            if (trade.getOrderId() == null || trade.getOrderStatus() == null) {
                throw new IllegalStateException("주문이 없는 거래입니다.");
            }

            OrderStatus currentOrder = trade.getOrderStatus();
            OrderStatus nextOrder = request.getOrderStatus();

            // 주문확인: PAID > DELIVERY_READY (CANCELED는 위 cancelRequested에서 이미 처리됨)
            validateOrderStatusTransition(currentOrder, nextOrder);

            mapper.updateOrderStatus(tradeId, nextOrder, now);
        }

        // 그 외 trade_status 변경은 이 API에서는 허용하지 않음
        if (request.getStatus() != null) {
            throw new IllegalStateException("허용되지 않는 거래 상태 변경입니다.");
        }

        TradeStatusUpdateResponse response = new TradeStatusUpdateResponse();
        response.setTradeId(tradeId);
        response.setTradeStatus(trade.getTradeStatus());
        response.setUpdatedAt(now);
        return response;
    }

    // 거래 내역 소프트 딜리트
    @Override
    @Transactional
    public void softDeleteTrade(Long tradeId,
                                String deletedBy,
                                Long memberId) {

        // 거래 조회
        TradeStatusUpdateRow trade = mapper.selectById(tradeId);
        if (trade == null) {
            throw new IllegalArgumentException("존재하지 않는 거래입니다.");
        }

        // 권한 검증
        if (!trade.getSellerId().equals(memberId) &&
            !trade.getBuyerId().equals(memberId)) {
            throw new IllegalStateException("해당 거래의 당사자가 아닙니다.");
        }

        // 현재 삭제한 사람 읽어오기
        boolean sellerDeleted = Boolean.TRUE.equals(trade.getSellerDeleted());
        boolean buyerDeleted = Boolean.TRUE.equals(trade.getBuyerDeleted());

        if ("SELLER".equals(deletedBy)) {
            // 판매자로서 삭제하는 경우는 seller_deleted 세팅
            if (!trade.getSellerId().equals(memberId)) {
                throw new IllegalStateException("판매자만 판매내역 삭제를 요청할 수 있습니다.");
            }
            sellerDeleted = true;
        } else if ("BUYER".equals(deletedBy)) {
            // 구매자로서 삭제하는 경우는 buyer_deleted 세팅
            if (!trade.getBuyerId().equals(memberId)) {
                throw new IllegalStateException("구매자만 구매내역 삭제를 요청할 수 있습니다.");
            }
            buyerDeleted = true;
        } else {
            throw new IllegalArgumentException("deletedBy 값이 올바르지 않습니다. SELLER 또는 BUYER 여야 합니다.");
        }

        LocalDateTime updatedAt = LocalDateTime.now();

        // 실제 업데이트 쿼리 호출
        mapper.updateSoftDelete(tradeId, sellerDeleted, buyerDeleted, updatedAt);
    }


    @Override
    @Transactional
    public Long processTradePendingBySeller(TradeRequest tradeRequest, Long memberId) {
        validateProductAndSeller(tradeRequest.getProductId(), memberId);

        Trade existTradeOfThisBuyer = mapper.existValidTradeByProductIdSellerId(
            tradeRequest.getProductId(),
            memberId,
            tradeRequest.getBuyerId()
        );

        if (existTradeOfThisBuyer != null && existTradeOfThisBuyer.getTradeId() > 0) {
            throw new ApiException(ErrorCode.TRADE_ALREADY_EXIST);
        }

        List<Trade> existProcessingTrades= mapper.existValidProcessingTradeByProductIdSellerId(
            tradeRequest.getProductId(),
            memberId,
            tradeRequest.getBuyerId()
        );

        if (!CollectionUtils.isEmpty(existProcessingTrades)) {
            throw new ApiException(ErrorCode.TRADE_PROCESSING_ALREADY_EXIST);
        }

        tradeRequest.setTradeType(TradeType.DIRECT);      // TODO: 지금은 일단 직거래!!! 확장 가능
        tradeRequest.setTradeStatus(TradeStatus.PENDING); // 예약 상태로 생성

        int tradeResult = mapper.createTrade(tradeRequest);
        if (tradeResult <= 0) {
            throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
        }

        int salesResult = productCommandMapper.updateProductStatus(tradeRequest.getProductId(), SalesStatus.RESERVED);
        if (salesResult <= 0) {
            throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
        }

        Long createdTradeId = tradeRequest.getTradeId();
        if (createdTradeId == null || createdTradeId <= 0) {
            throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
        }

        return createdTradeId;
    }

    @Override
    @Transactional
    public Long processTradeCompleteBySeller(TradeRequest tradeRequest, Long memberId) {

        validateProductAndSeller(tradeRequest.getProductId(), memberId);

        Trade existTrade = mapper.existValidTradeByProductIdSellerId(
                tradeRequest.getProductId(),
                memberId,
                tradeRequest.getBuyerId()
            );

        if (existTrade != null && existTrade.getTradeStatus() == TradeStatus.COMPLETED) {
            // 이미 완료된 거래
            throw new ApiException(ErrorCode.TRADE_COMPLETED_ALREADY_EXIST);
        }

        List<Trade> existProcessingTrades= mapper.existValidProcessingTradeByProductIdSellerId(
            tradeRequest.getProductId(),
            memberId,
            tradeRequest.getBuyerId()
        );

        if (!CollectionUtils.isEmpty(existProcessingTrades)) {
            throw new ApiException(ErrorCode.TRADE_PROCESSING_ALREADY_EXIST);
        }

        if (existTrade == null || existTrade.getTradeId() <= 0) {
            tradeRequest.setTradeType(TradeType.DIRECT);
            tradeRequest.setTradeStatus(TradeStatus.COMPLETED); // 바로 완료 상태로 생성

            int result = mapper.createTrade(tradeRequest);

            if (result <= 0) {
                throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
            }

            int salesResult = productCommandMapper.updateProductStatus(
                tradeRequest.getProductId(),
                SalesStatus.SOLD_OUT);
            if (salesResult <= 0) {
                throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
            }

            Long createdTradeId = tradeRequest.getTradeId();
            if (createdTradeId == null || createdTradeId <= 0) {
                throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
            }

            return createdTradeId; // 이미 COMPLETED로 생성했으니 여기서 끝낼 수도 있음
        }

        // 기존 거래가 있으면 상태만 COMPLETED로 변경
        mapper.updateTradeStatus(
            existTrade.getTradeId(),
            TradeStatus.COMPLETED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            LocalDateTime.now()
        );

        int salesResult = productCommandMapper.updateProductStatus(
            tradeRequest.getProductId(),
            SalesStatus.SOLD_OUT);
        if (salesResult <= 0) {
            throw new ApiException(ErrorCode.TRADE_CREATE_FAILED);
        }

        return existTrade.getTradeId();
    }

    private ProductBasicInfo validateProductAndSeller(Long productId, Long memberId) {
        ProductBasicInfo productBasicInfo =
            productQueryMapper.selectBasicInfo(productId);

        if (productBasicInfo == null) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (productBasicInfo.getSalesStatus() == SalesStatus.SOLD_OUT) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (!Objects.equals(productBasicInfo.getSellerId(), memberId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        return productBasicInfo;
    }


    /*
     주문(order) 상태 전이 검증
     - 주문확인: PAID > DELIVERY_READY
     - 배송 흐름: DELIVERY_READY > SHIPPED > DELIVERED
     - 취소: PAID/DELIVERY_READY 에서 CANCELED 허용
     */
    private void validateOrderStatusTransition(OrderStatus current, OrderStatus next) {
        boolean allowed = switch (current) {
            case PAID -> (next == OrderStatus.DELIVERY_READY || next == OrderStatus.CANCELED);
            case DELIVERY_READY -> (next == OrderStatus.SHIPPED || next == OrderStatus.DELIVERED || next == OrderStatus.CANCELED);
            case SHIPPED -> (next == OrderStatus.DELIVERED);
            case DELIVERED, CANCELED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException("허용되지 않는 주문 상태 변경입니다.");
        }
    }

    private void cancelTradeAndOrder(
        TradeStatusUpdateRow trade,
        Long tradeId,
        Long memberId,
        LocalDateTime now
    ) {
        boolean isSeller = memberId.equals(trade.getSellerId());
        boolean isBuyer = memberId.equals(trade.getBuyerId());

        // canceled_by
        String canceledBy;
        if (isSeller) canceledBy = "SELLER";
        else if (isBuyer) canceledBy = "BUYER";
        else throw new IllegalStateException("해당 거래의 당사자가 아닙니다.");

        /* 취소 가능 조건 강제
           - order 없는 채팅 - 직거래: trade_status == PENDING 일 때만
           - order 있는 바로구매: order_status == PAID 일 때만 */
        if (trade.getOrderId() == null || trade.getOrderStatus() == null) {
            // 채팅거래
            if (trade.getTradeStatus() != TradeStatus.PENDING) {
                throw new IllegalStateException("채팅거래는 예약중(PENDING)일 때만 취소할 수 있습니다.");
            }
        } else {
            // 바로구매
            if (trade.getOrderStatus() != OrderStatus.PAID) {
                throw new IllegalStateException("바로구매 주문 취소는 결제완료(PAID)일 때만 가능합니다.");
            }
        }

        // order도 같이 취소 (바로구매인 경우에만)
        if (trade.getOrderId() != null && trade.getOrderStatus() != null) {

            mapper.updateOrderStatus(tradeId, OrderStatus.CANCELED, now); // 취소되면 order_status = CANCELED, updated_at = now
        }

        // 상품 판매상태 복구
        if (trade.getProductId() != null) {
            mapper.updateProductSalesStatus(trade.getProductId(), SalesStatus.FOR_SALE);
        }

        // trade 취소 처리: status + canceled_at + canceled_by + updated_at
        mapper.updateTradeStatus(
            tradeId,
            TradeStatus.CANCELED,
            trade.getCompletedAt(), // completed_at은 유지
            now, // canceled_at
            canceledBy,
            now // updated_at
        );
    }

    private void completeTradeAndOrderIfNeeded(
        TradeStatusUpdateRow trade,
        Long tradeId,
        LocalDateTime now
    ) {
        // 완료 가능 조건 강제 (판매자만 여기로 들어오게 updateTradeStatus에서 막음)

        // 상품 SOLD_OUT
        if (trade.getProductId() != null) {
            mapper.updateProductSalesStatus(trade.getProductId(), SalesStatus.SOLD_OUT);
        }

        // 바로구매: order 정합성 맞추기
        if (trade.getOrderId() != null && trade.getOrderStatus() != null) {
            TradeType tradeType = trade.getTradeType();
            OrderStatus currentOrder = trade.getOrderStatus();

            if (tradeType == TradeType.DIRECT) {
                // 직거래: 주문확인 이후일 것이고, 완료시 DELIVERED로 바꿔줘야 함
                if (currentOrder != OrderStatus.DELIVERY_READY) {
                    throw new IllegalStateException("직거래 바로구매는 주문확인(DELIVERY_READY)일 때만 거래완료가 가능합니다.");
                }

                validateOrderStatusTransition(currentOrder, OrderStatus.DELIVERED);
                mapper.updateOrderStatus(tradeId, OrderStatus.DELIVERED, now);

            } else if (tradeType == TradeType.DELIVERY) {
                // 택배거래: 이미 DELIVERED일 것
                if (currentOrder != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("택배거래는 배송완료(DELIVERED)일 때만 거래완료가 가능합니다.");
                }
            } else {
                throw new IllegalStateException("알 수 없는 거래 타입입니다.");
            }
        } else {
            // 채팅 - 직거래: trade_status == PENDING일 때만 완료 허용
            if (trade.getTradeStatus() != TradeStatus.PENDING) {
                throw new IllegalStateException("채팅거래는 예약중(PENDING)일 때만 거래완료가 가능합니다.");
            }
        }

        // trade 완료 처리: status + completed_at + updated_at
        mapper.updateTradeStatus(
            tradeId,
            TradeStatus.COMPLETED,
            now, // completed_at
            trade.getCanceledAt(),
            trade.getCanceledBy(),
            now // updated_at
        );
    }


    @Override
    public TradeReviewInfoDto getTradeInfoForReview(Long tradeId, Long loginMemberId) {
        TradeReviewInfoDto dto = mapper.selectTradeInfoForReview(tradeId, loginMemberId);
//        TODO: 접근자가 거래 참여자인지 검증 ?
        if (dto == null) {
            throw new ApiException(ErrorCode.TRADE_NOT_FOUND);
        }

        return dto;
    }
}
