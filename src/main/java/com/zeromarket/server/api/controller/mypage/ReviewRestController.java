package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.PageResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewCursorResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryResponse;
import com.zeromarket.server.api.dto.mypage.ReviewCreateRequest;
import com.zeromarket.server.api.dto.mypage.ReviewListResponse;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.mypage.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Board API", description = "게시판 CRUD API 콘트라베이스")
public class ReviewRestController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     * @param dto
     * @param userDetails
     * @return
     */
    @Operation(summary = "리뷰 작성", description = "")
    @PostMapping
    public ResponseEntity<Long> createReview(
        @RequestBody ReviewCreateRequest dto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(dto, userDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    /**
     * review id로 리뷰 단건 조회
     * @param reviewId
     * @return
     */
    @Operation(summary = "review id로 리뷰 단건 조회", description = "")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ReviewResponse dto = reviewService.getReviewById(reviewId, userDetails.getMemberId());
        return ResponseEntity.ok(dto);
    }

    /**
     * 마이페이지 > 받은 리뷰 요약
     * @param userDetails
     * @return
     */
    @Operation(summary = "특정 회원이 받은 리뷰 요약 목록", description = "rating당 3개씩 조회, 최신순")
    @GetMapping("/received/summary") // /received/summary/{memberId}
    public ResponseEntity<ReceivedReviewSummaryResponse> getReceivedReviewSummary(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ReceivedReviewSummaryResponse summaryResponse = reviewService.getReceivedReviewSummary(userDetails.getMemberId());
        return ResponseEntity.ok(summaryResponse);
    }

    /**
     * 마이페이지 > 받은 리뷰 - 점수별
     * @param userDetails
     * @param rating
     * @param size
     * @return
     */
    @GetMapping("/received") // /received/{memberId}
    public ResponseEntity<ReceivedReviewCursorResponse> getReceivedReviewsByRating(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam Integer rating,
        @RequestParam(required = false) Long cursorReviewId,
        @RequestParam(required = false)
        @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            reviewService.getReceivedReviewsByRating(
                userDetails.getMemberId(), rating, cursorReviewId, cursorCreatedAt, size)
        );
    }

    /**
     * 신뢰점수
     * @param memberId
     * @return
     */
    @Operation(summary = "신뢰점수", description = "후기 점수 평균")
    @GetMapping("/member/{memberId}/average-rating")
    public ResponseEntity<Double> getTrustScore(
        @PathVariable Long memberId
    ) {
        Double trustScore = reviewService.getTrustScore(memberId);
        return ResponseEntity.ok(trustScore);
    }

    /**
     * 마이페이지에서 보여줄 받은 후기 숫자 카운트
     * @param memberId
     * @return
     */
    @Operation(summary = "마이페이지에서 보여줄 받은 후기 숫자 카운트", description = "마이페이지에서 보여줄 받은 후기 숫자 카운트")
    @GetMapping("/received/count/{memberId}")
    public ResponseEntity<Integer> getCountReceivedReviewsOnMyPage(
        @PathVariable Long memberId
    ) {
        Integer count = reviewService.getCountReceivedReviewsOnMyPage(memberId);
        return ResponseEntity.ok(count);
    }
}
