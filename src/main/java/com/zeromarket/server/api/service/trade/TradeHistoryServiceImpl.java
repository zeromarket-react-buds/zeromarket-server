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

    // 거래 상태 업데이트
    @Override
    @Transactional
    public TradeStatusUpdateResponse updateTradeStatus(Long tradeId,
                                                       TradeStatusUpdateRequest request,
                                                       Long memberId) {

        TradeStatusUpdateRow trade = mapper.selectById(tradeId);
        if (trade == null) throw new IllegalArgumentException("존재하지 않는 거래입니다.");

        if (!trade.getSellerId().equals(memberId) && !trade.getBuyerId().equals(memberId)) {
            throw new IllegalStateException("해당 거래의 당사자가 아닙니다.");
        }

        LocalDateTime updatedAt = LocalDateTime.now();

        // 1) 주문 상태 변경이 들어온 경우 (바로구매 진행)
        if (request.getOrderStatus() != null) {
            if (trade.getOrderId() == null || trade.getOrderStatus() == null) {
                throw new IllegalStateException("주문이 없는 거래입니다.");
            }

            OrderStatus currentOrder = trade.getOrderStatus();
            OrderStatus nextOrder = request.getOrderStatus();

            validateOrderStatusTransition(currentOrder, nextOrder);

            mapper.updateOrderStatus(tradeId, nextOrder, updatedAt);

            // 선택: 주문이 CANCELED로 바뀌면 trade도 같이 CANCELED로 맞춰야 한다면
            // -> 아래 trade 취소 로직을 재사용해서 trade_status도 CANCELED 처리하는 방식 권장
            // (이 부분은 정책이라서 확실하지 않음: 원하시는 동작이 “주문취소=거래취소”가 맞는지)
        }

        // 2) 거래 상태 변경이 들어온 경우 (채팅거래 종료 or 거래 완료/취소)
        if (request.getStatus() != null) {
            TradeStatus current = trade.getTradeStatus();
            TradeStatus target = request.getStatus();

            validateStatusTransition(current, target);

            LocalDateTime completedAt = trade.getCompletedAt();
            LocalDateTime canceledAt = trade.getCanceledAt();
            String canceledBy  = trade.getCanceledBy();

            if (target == TradeStatus.COMPLETED) {
                completedAt = updatedAt;
                Long productId = trade.getProductId();
                if (productId != null) {
                    mapper.updateProductSalesStatus(productId, SalesStatus.SOLD_OUT);
                }
            } else if (target == TradeStatus.CANCELED) {
                canceledAt = updatedAt;

                if (memberId.equals(trade.getSellerId())) canceledBy = "SELLER";
                else if (memberId.equals(trade.getBuyerId())) canceledBy = "BUYER";

                Long productId = trade.getProductId();
                if (productId != null) {
                    mapper.updateProductSalesStatus(productId, SalesStatus.FOR_SALE);
                }
            }

            mapper.updateTradeStatus(tradeId, target, completedAt, canceledAt, canceledBy, updatedAt);
        }

        // 응답은 최소로 유지하되, 프론트가 필요하면 orderStatus도 같이 내려주기 권장
        TradeStatusUpdateResponse response = new TradeStatusUpdateResponse();
        response.setTradeId(tradeId);
        response.setTradeStatus(request.getStatus() != null ? request.getStatus() : trade.getTradeStatus());
        response.setUpdatedAt(updatedAt);

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

    private void validateStatusTransition(
        TradeStatus current,
        TradeStatus target
    ) {
        // 예: PENDING > COMPLETED, PENDING > CANCELED 둘 다 허용
        if (current == TradeStatus.PENDING &&
            (target == TradeStatus.COMPLETED || target == TradeStatus.CANCELED)) {
            return;
        }
        // 그 외에는 불허
        throw new IllegalStateException("허용되지 않는 상태 변경입니다.");
    }

    /**
     * 주문(order) 상태 전이 검증
     * - 주문 도메인은 별도 담당
     * - 거래 내역 화면/흐름 검증용으로 쓰임
     */
    private void validateOrderStatusTransition(
        OrderStatus current,
        OrderStatus next
    ) {
        boolean allowed = switch (current) {
            case PAID -> (next == OrderStatus.DELIVERY_READY || next == OrderStatus.CANCELED);
            case DELIVERY_READY -> (next == OrderStatus.SHIPPED);
            case SHIPPED -> (next == OrderStatus.DELIVERED);
            case DELIVERED, CANCELED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException("허용되지 않는 주문 상태 변경입니다.");
        }
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
