package com.zeromarket.server.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private Long reviewId;
    private Long writerId;
    private Long tradeId;
    private String reviewedBy;     // SELLER, BUYER
    private Integer rating;        // 1-5
    private String content;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
