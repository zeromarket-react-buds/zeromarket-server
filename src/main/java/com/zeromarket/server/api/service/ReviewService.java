package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.ReviewCreateRequest;
import com.zeromarket.server.common.entity.Review;
import java.util.List;

public interface ReviewService {

    /**
     * 리뷰 생성
     * @param review 생성할 리뷰 정보
     * @return 생성된 리뷰 ID
     */
    Long createReview(ReviewCreateRequest review);

    /**
     * 리뷰 ID로 조회
     * @param reviewId 리뷰 ID
     * @return 리뷰 정보
     */
    Review getReviewById(Long reviewId);

    /**
     * 모든 리뷰 조회
     * @return 리뷰 목록
     */
    List<Review> getAllReviews();

    /**
     * 작성자 ID로 리뷰 조회 (내가 작성한 리뷰)
     * @param writerId 작성자 ID
     * @return 리뷰 목록
     */
    List<Review> getReviewsByWriterId(Long writerId);

    /**
     * 거래 ID로 리뷰 조회
     * @param tradeId 거래 ID
     * @return 리뷰 목록
     */
    List<Review> getReviewsByTradeId(Long tradeId);

    /**
     * 특정 회원이 받은 리뷰 조회
     * @param memberId 회원 ID
     * @return 리뷰 목록
     */
    List<Review> getReviewsByMemberId(Long memberId);

    /**
     * 평점별 리뷰 조회
     * @param rating 평점 (1-5)
     * @return 리뷰 목록
     */
    List<Review> getReviewsByRating(Integer rating);

    /**
     * 회원의 평균 평점 조회
     * @param memberId 회원 ID
     * @return 평균 평점
     */
    Double getAverageRatingByMemberId(Long memberId);

    /**
     * 리뷰 수정
     * @param review 수정할 리뷰 정보
     * @return 수정된 행 수
     */
    int updateReview(Review review);

    /**
     * 리뷰 삭제 (소프트 삭제)
     * @param reviewId 리뷰 ID
     * @return 삭제된 행 수
     */
    int deleteReview(Long reviewId);

    /**
     * 리뷰 완전 삭제
     * @param reviewId 리뷰 ID
     * @return 삭제된 행 수
     */
    int hardDeleteReview(Long reviewId);
}
