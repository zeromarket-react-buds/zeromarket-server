package com.zeromarket.server.api.service.order;

import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;
import com.zeromarket.server.api.dto.order.*;
import com.zeromarket.server.api.mapper.mypage.ReviewMapper;
import com.zeromarket.server.api.mapper.order.TradeHistoryMapper;
import com.zeromarket.server.common.entity.Review;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TradeHistoryServiceImpl implements TradeHistoryService{

    private final TradeHistoryMapper mapper;
    private final ReviewMapper reviewMapper;

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
    public List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req) {

        // 컨트롤러에서 세팅해준 로그인 회원 id
        Long loginMemberId = req.getMemberId();

        // id랑 role에 의하 검색/필터용
        TradeHistoryRequest tradeReq = new TradeHistoryRequest();
        tradeReq.setMemberId(loginMemberId);
        tradeReq.setRole(req.getRole());
        tradeReq.setKeyword(req.getKeyword());

        // 거래 목록 조회
        List<TradeHistoryResponse> histories = mapper.selectTradeList(tradeReq);

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
    public TradeReviewInfoDto getTradeInfoForReview(Long tradeId, Long loginMemberId) {
        TradeReviewInfoDto dto = mapper.selectTradeInfoForReview(tradeId, loginMemberId);
//        TODO: 접근자가 거래 참여자인지 검증 ?
        if (dto == null) {
            throw new ApiException(ErrorCode.TRADE_NOT_FOUND);
        }

        return dto;
    }
}
