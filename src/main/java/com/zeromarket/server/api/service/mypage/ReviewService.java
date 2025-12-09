package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewCursorResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryResponse;
import com.zeromarket.server.api.dto.mypage.ReviewCreateRequest;
import com.zeromarket.server.api.dto.mypage.ReviewListResponse;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;
import java.time.LocalDateTime;

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
     * @param cursorReviewId
     * @param size
     * @return
     */
    ReceivedReviewCursorResponse getReceivedReviewsByRating(
        Long memberId,
        Integer rating,
        Long cursorReviewId,
        LocalDateTime cursorCreatedAt,
        int size
    );

    /**
     * 신뢰점수
     * @param memberId
     * @return
     */
    public double getTrustScore(Long memberId);

}
