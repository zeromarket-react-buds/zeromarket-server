package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.dto.mypage.RatingReviewGroup;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewCursorResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryDto;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryResponse;
import com.zeromarket.server.api.dto.mypage.ReviewCreateRequest;
import com.zeromarket.server.api.dto.mypage.ReviewListResponse;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.ReviewMapper;
import com.zeromarket.server.api.mapper.order.TradeHistoryMapper;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.entity.Review;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.exception.ApiException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final TradeHistoryMapper tradeMapper;
    private final MemberMapper memberMapper;

    /**
     * 리뷰 생성
     */
    @Transactional
    @Override
    public Long createReview(ReviewCreateRequest dto, Long loginMemberId) {

        // 1) 거래 정보 조회
        Map<String, Object> info = tradeMapper.selectSellerBuyerStatusByTradeId(dto.getTradeId());
        if (info == null) {
            throw new ApiException(ErrorCode.TRADE_NOT_FOUND);
        }

        Long sellerId = ((Number) info.get("sellerId")).longValue();
        Long buyerId  = ((Number) info.get("buyerId")).longValue();
        TradeStatus tradeStatus = (TradeStatus) info.get("tradeStatus");

        // 2) 거래 참여자 검증
        if (!loginMemberId.equals(sellerId) && !loginMemberId.equals(buyerId)) {
            throw new ApiException(ErrorCode.REVIEW_CREATE_FORBIDDEN);
//            throw new IllegalArgumentException("거래에 참여한 사용자만 리뷰를 작성할 수 있습니다.");
        }

        // 2) 거래 상태 검증
        if (tradeStatus != TradeStatus.COMPLETED) {
//        if (!TradeStatus.COMPLETED.toString().equals(tradeStatus)) {
            throw new IllegalArgumentException("완료된 거래만 리뷰를 작성할 수 있습니다.");
        }

        // 3) 중복 작성 방지
        if (reviewMapper.existsReviewByWriter(dto.getTradeId(), loginMemberId)) {
            throw new ApiException(ErrorCode.REVIEW_EXIST);
        }

        // 4) reviewed_by 판별
        String reviewedBy = loginMemberId.equals(sellerId)
            ? "SELLER"
            : "BUYER";

        // 5) 저장 로직
        Review entity = new Review();
        entity.setTradeId(dto.getTradeId());
        entity.setWriterId(loginMemberId);
        entity.setReviewedBy(reviewedBy);
        entity.setRating(dto.getRating());
        entity.setContent(dto.getContent());

        reviewMapper.insertReview(entity);
        return entity.getReviewId();
    }

    /**
     * 리뷰 ID로 조회
     */
    public ReviewResponse getReviewById(Long reviewId, Long memberId) {
        // 1) 리뷰 조회
        ReviewResponse entity = reviewMapper.selectReviewById(reviewId);
        if (entity == null) {throw new ApiException(ErrorCode.REVIEW_NOT_FOUND);}

        // 2) 거래 조회
        Map<String, Object> sellerBuyerStatus = tradeMapper.selectSellerBuyerStatusByTradeId(entity.getTradeId());
        if(sellerBuyerStatus == null){throw new ApiException(ErrorCode.TRADE_NOT_FOUND);}

        // 3) 거래 참여자인지 확인
        if (!memberId.equals(sellerBuyerStatus.get("sellerId")) && !memberId.equals(sellerBuyerStatus.get("buyerId"))) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        return entity;
//        ReviewResponse dto = new ReviewResponse();
//        BeanUtils.copyProperties(entity, dto);
//
//        return dto;
    }

    /**
     * 특정 회원이 받은 후기 목록 (요약)
     * (점수별 3건만, 최신순, 5,4점만)
     */
    @Override
    public ReceivedReviewSummaryResponse getReceivedReviewSummary(Long memberId) {
        // 닉네임
        Member member = memberMapper.selectMemberById(memberId);
        if(member == null) throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);

        // 리스트 조회 (rating5 + rating4 혼합 형태)
        List<ReceivedReviewSummaryDto> list =
            reviewMapper.selectReceivedReviewSummary(memberId);

        // 분리
        List<ReceivedReviewSummaryDto> rating5 =
            list.stream().filter(r -> r.getRating() == 5).toList();

        List<ReceivedReviewSummaryDto> rating4 =
            list.stream().filter(r -> r.getRating() == 4).toList();

        ReceivedReviewSummaryResponse response = new ReceivedReviewSummaryResponse();
        response.setRating5(new RatingReviewGroup(5, rating5.size(), rating5));
        response.setRating4(new RatingReviewGroup(4, rating4.size(), rating4));
        response.setNickname(member.getNickname());

        return response;
    }

    /**
     * 특정 회원이 받은 후기 목록 (점수별)
     * (점수별 + 페이징)
     */
    @Override
    public ReceivedReviewCursorResponse getReceivedReviewsByRating(
        Long memberId,
        Integer rating,
        Long cursorReviewId,
        LocalDateTime cursorCreatedAt,
        int size
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("rating", rating);
        params.put("cursorReviewId", cursorReviewId);
        params.put("cursorCreatedAt", cursorCreatedAt);
        params.put("size", size);

        List<ReviewListResponse> items =
            reviewMapper.selectReceivedReviewsByRating(params);

        boolean hasNext = items.size() == size;

        Long nextCursorReviewId = null;
        LocalDateTime nextCursorCreatedAt = null;

        if (hasNext) {
            ReviewListResponse last = items.get(items.size() - 1);
            nextCursorReviewId = last.getReviewId();
            nextCursorCreatedAt = last.getCreatedAt();
        }

        return new ReceivedReviewCursorResponse(
            items,
            nextCursorReviewId,
            nextCursorCreatedAt,
            hasNext
        );
    }

    /**
     * 신뢰점수
     * @param memberId
     * @return
     */
    @Override
    public double getTrustScore(Long memberId) {
        Double avgRating = reviewMapper.getAvgRating(memberId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public int getCountReceivedReviewsOnMyPage(Long memberId) {
        return reviewMapper.countReceivedReviewsOnMyPage(memberId);
    }

    /**
     * 모든 리뷰 조회
     */
    public List<Review> getAllReviews() {
        return reviewMapper.selectAllReviews();
    }

    /**
     * 작성자 ID로 리뷰 조회 (내가 작성한 리뷰)
     */
    public List<Review> getReviewsByWriterId(Long writerId) {
        return reviewMapper.selectReviewsByWriterId(writerId);
    }

    /**
     * 거래 ID로 리뷰 조회
     */
    public List<Review> getReviewsByTradeId(Long tradeId) {
        return reviewMapper.selectReviewsByTradeId(tradeId);
    }

    /**
     * 특정 회원이 받은 리뷰 조회
     */
    public List<Review> getReviewsByMemberId(Long memberId) {
        return reviewMapper.selectReviewsByMemberId(memberId);
    }

    /**
     * 평점별 리뷰 조회
     */
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewMapper.selectReviewsByRating(rating);
    }

    /**
     * 회원의 평균 평점 조회
     */
    public Double getAverageRatingByMemberId(Long memberId) {
        return reviewMapper.selectAverageRatingByMemberId(memberId);
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public int updateReview(Review review) {
        // 평점 유효성 검사
        if (review.getRating() != null &&
            (review.getRating() < 1 || review.getRating() > 5)) {
            throw new IllegalArgumentException("평점은 1-5 사이여야 합니다.");
        }

        return reviewMapper.updateReview(review);
    }

    /**
     * 리뷰 삭제 (소프트 삭제)
     */
    @Transactional
    public int deleteReview(Long reviewId) {
        return reviewMapper.deleteReview(reviewId);
    }

    /**
     * 리뷰 완전 삭제
     */
    @Transactional
    public int hardDeleteReview(Long reviewId) {
        return reviewMapper.hardDeleteReview(reviewId);
    }
}
