package com.zeromarket.server.api.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    private Long writerId;
    private Long tradeId;
    private String reviewedBy;  // SELLER, BUYER
    private Integer rating;     // 1-5
    private String content;
}
