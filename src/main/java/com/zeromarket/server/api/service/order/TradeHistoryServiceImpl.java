package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.*;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.mapper.mypage.ReviewMapper;
import com.zeromarket.server.api.mapper.order.TradeHistoryMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.common.entity.Review;
import com.zeromarket.server.common.entity.Trade;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import com.zeromarket.server.common.exception.ApiException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TradeHistoryServiceImpl implements TradeHistoryService{

    private final TradeHistoryMapper mapper;
    private final ReviewMapper reviewMapper;
    private final ProductQueryMapper productQueryMapper;

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

    @Override
    @Transactional
    public TradeStatusUpdateResponse updateTradeStatus(Long tradeId,
                                                       TradeStatusUpdateRequest request,
                                                       Long memberId) {

        // 거래 조회
        TradeStatusUpdateRow trade = mapper.selectById(tradeId);
        if (trade == null) {
            // 존재하지 않으면 예외
            throw new IllegalArgumentException("존재하지 않는 거래입니다.");
        }

        // 권한 검증
        if (!trade.getSellerId().equals(memberId) &&
            !trade.getBuyerId().equals(memberId)) {
            throw new IllegalStateException("해당 거래의 당사자가 아닙니다.");
        }

        // 상태 전이 검증
        TradeStatus current = trade.getTradeStatus();
        TradeStatus target = request.getStatus();

        validateStatusTransition(current, target, memberId, trade);

        // 상태 및 시간 컬럼 세팅
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime completedAt = trade.getCompletedAt();

        if (target == TradeStatus.COMPLETED) {
            completedAt = updatedAt;
        }

        mapper.updateTradeStatus(tradeId, target, completedAt, updatedAt);

        // 응답 DTO 구성
        TradeStatusUpdateResponse response = new TradeStatusUpdateResponse();
        response.setTradeId(tradeId);
        response.setTradeStatus(target);
        response.setCompletedAt(completedAt);

        return response;
    }

    @Override
    @Transactional
    public Long processTradePendingBySeller(TradeRequest tradeRequest, Long memberId) {
        validateProductAndSeller(tradeRequest.getProductId(), memberId);

        Trade existTrade = mapper.existValidTradeByProductIdSellerId(
            tradeRequest.getProductId(),
            memberId,
            tradeRequest.getBuyerId()
        );

        if (existTrade != null && existTrade.getTradeId() > 0) {
            throw new ApiException(ErrorCode.TRADE_ALREADY_EXIST);
        }

        tradeRequest.setTradeType(TradeType.DIRECT);      // TODO: 지금은 일단 직거래!!! 확장 가능
        tradeRequest.setTradeStatus(TradeStatus.PENDING); // 예약 상태로 생성

        int result = mapper.createTrade(tradeRequest);
        if (result <= 0) {
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
            throw new ApiException(ErrorCode.TRADE_ALREADY_EXIST);
        }

        if (existTrade == null || existTrade.getTradeId() <= 0) {
            tradeRequest.setTradeType(TradeType.DIRECT);
            tradeRequest.setTradeStatus(TradeStatus.COMPLETED); // 바로 완료 상태로 생성

            int result = mapper.createTrade(tradeRequest);

            if (result <= 0) {
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
            LocalDateTime.now()
        );

        return existTrade.getTradeId();
    }

    private ProductBasicInfo validateProductAndSeller(Long productId, Long memberId) {
        ProductBasicInfo productBasicInfo =
            productQueryMapper.selectBasicInfo(productId);

        if (productBasicInfo == null) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (!Objects.equals(productBasicInfo.getSellerId(), memberId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        return productBasicInfo;
    }

    private void validateStatusTransition(TradeStatus current,
                                          TradeStatus target,
                                          Long memberId,
                                          TradeStatusUpdateRow trade) {

        // 예: PENDING → COMPLETED
        if (current == TradeStatus.PENDING) {
            if (target == TradeStatus.COMPLETED) {
                return;
            }
        }

        // 그 외에는 불허
        throw new IllegalStateException("허용되지 않는 상태 변경입니다.");
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
