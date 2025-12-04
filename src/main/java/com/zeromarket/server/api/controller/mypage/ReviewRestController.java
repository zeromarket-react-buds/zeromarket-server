package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.ReviewCreateRequest;
import com.zeromarket.server.api.dto.mypage.ReviewResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.common.entity.Review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        ReviewResponse dto = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 모든 리뷰 조회
     */
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * 작성자 ID로 리뷰 조회 (내가 작성한 리뷰)
     */
    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회")
    @GetMapping("/writer/{writerId}")
    public ResponseEntity<List<Review>> getReviewsByWriterId(@PathVariable Long writerId) {
        List<Review> reviews = reviewService.getReviewsByWriterId(writerId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 거래 ID로 리뷰 조회
     */
    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회")
    @GetMapping("/trade/{tradeId}")
    public ResponseEntity<List<Review>> getReviewsByTradeId(@PathVariable Long tradeId) {
        List<Review> reviews = reviewService.getReviewsByTradeId(tradeId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 회원이 받은 리뷰 조회
     */
    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 단건 조회")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Review>> getReviewsByMemberId(@PathVariable Long memberId) {
        List<Review> reviews = reviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 평점별 리뷰 조회
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<Review>> getReviewsByRating(@PathVariable Integer rating) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().build();
        }
        List<Review> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 회원의 평균 평점 조회
     */
    @GetMapping("/member/{memberId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByMemberId(@PathVariable Long memberId) {
        Double averageRating = reviewService.getAverageRatingByMemberId(memberId);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * 리뷰 수정
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
        @PathVariable Long reviewId,
        @RequestBody Review review) {
        try {
            review.setReviewId(reviewId);
            int result = reviewService.updateReview(review);
            if (result > 0) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 리뷰 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        int result = reviewService.deleteReview(reviewId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 리뷰 완전 삭제
     */
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<Void> hardDeleteReview(@PathVariable Long reviewId) {
        int result = reviewService.hardDeleteReview(reviewId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
