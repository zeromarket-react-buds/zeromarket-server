package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryDto;
import com.zeromarket.server.api.dto.mypage.ReviewListResponse;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;
import com.zeromarket.server.common.entity.Review;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {

    // 리뷰 생성
    int insertReview(Review review);

    // 리뷰 중복 작성 확인
    boolean existsReviewByWriter(
        @Param("tradeId") Long tradeId,
        @Param("writerId") Long writerId
    );

    // 리뷰 ID로 조회
    ReviewResponse selectReviewById(@Param("reviewId") Long reviewId);

    // 특정 회원이 받은 후기 목록 (요약, 5/4점, 최신순, 3건)
    List<ReceivedReviewSummaryDto> selectReceivedReviewSummary(@Param("memberId") Long memberId);

    // 특정 회원이 받은 후기 목록 (점수별)
    List<ReviewListResponse> selectReceivedReviewsByRating(
        Map<String, Object> params
    );

    // 특정 회원이 받은 후기 목록 (점수별) - 총 개수
    int countReceivedReviewsByRating(Map<String, Object> params);

    Double getAvgRating(@Param("memberId") Long memberId);

    // 모든 리뷰 조회
    List<Review> selectAllReviews();

    // 작성자 ID로 리뷰 조회 (내가 작성한 리뷰)
    List<Review> selectReviewsByWriterId(@Param("writerId") Long writerId);

    // 거래 ID로 리뷰 조회
    List<Review> selectReviewsByTradeId(@Param("tradeId") Long tradeId);

    // 특정 회원이 받은 리뷰 조회 (판매자 또는 구매자로서)
    List<Review> selectReviewsByMemberId(@Param("memberId") Long memberId);

    // 평점별 리뷰 조회
    List<Review> selectReviewsByRating(@Param("rating") Integer rating);

    // 회원의 평균 평점 조회
    Double selectAverageRatingByMemberId(@Param("memberId") Long memberId);

    // 리뷰 수정
    int updateReview(Review review);

    // 리뷰 삭제 (소프트 삭제)
    int deleteReview(@Param("reviewId") Long reviewId);

    // 리뷰 완전 삭제
    int hardDeleteReview(@Param("reviewId") Long reviewId);

}
