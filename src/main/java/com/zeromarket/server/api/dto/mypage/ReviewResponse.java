package com.zeromarket.server.api.dto.mypage;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long writerId;
    private String writerNickname;  // 작성자 닉네임
    private Long tradeId;
    private String reviewedBy;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String opponentNickname; // 추가
    private String productTitle;     // 추가
}
