package com.zeromarket.server.api.dto.mypage;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewListResponse {
    private Long reviewId;
    private Integer rating;
    private String content;
    private String writerNickname;
    private LocalDateTime createdAt;
}
