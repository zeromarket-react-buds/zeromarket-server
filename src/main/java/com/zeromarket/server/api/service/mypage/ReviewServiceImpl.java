package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.mapper.mypage.ReviewMapper;
import com.zeromarket.server.common.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    /**
     * 리뷰 생성
     */
    @Transactional
    public Long createReview(Review review) {
        // 평점 유효성 검사
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("평점은 1-5 사이여야 합니다.");
        }

        reviewMapper.insertReview(review);
        return review.getReviewId();
    }

    /**
     * 리뷰 ID로 조회
     */
    public Review getReviewById(Long reviewId) {
        return reviewMapper.selectReviewById(reviewId);
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
