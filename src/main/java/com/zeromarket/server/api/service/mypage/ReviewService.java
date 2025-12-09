package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryResponse;
import com.zeromarket.server.api.dto.mypage.ReviewCreateRequest;
import com.zeromarket.server.api.dto.mypage.ReviewListResponse;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;

public interface ReviewService {

    /**
     *
     * @param dto
     * @param loginMemberId
     * @return
     */
    Long createReview(ReviewCreateRequest dto, Long loginMemberId);

    /**
     * 리뷰 ID로 조회
     * @param reviewId 리뷰 ID
     * @return 리뷰 정보
     */
    ReviewResponse getReviewById(Long reviewId, Long memberId);

    /**
     * 특정 회원이 받은 후기 목록 (요약)
     * - 점수별 3건만, 최신순, 5,4점만
     * @param memberId
     * @return
     */
    ReceivedReviewSummaryResponse getReceivedReviewSummary(Long memberId);

    /**
     * 특정 회원이 받은 후기 목록 (점수별 + 페이징)
     * @param memberId
     * @param rating
     * @param page
     * @param size
     * @return
     */
    PageResponse<ReviewListResponse> getReceivedReviewsByRating(
        Long memberId, Integer rating, int page, int size
    );

    /**
     * 신뢰점수
     * @param memberId
     * @return
     */
    public double getTrustScore(Long memberId);

    /**
     * 마이페이지에서 보여줄 받은 후기 갯수
     * @param memberId
     * @return
     */
    public int getCountReceivedReviewsOnMyPage(Long memberId);
}
