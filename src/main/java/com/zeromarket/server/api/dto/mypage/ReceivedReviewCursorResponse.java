package com.zeromarket.server.api.dto.mypage;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceivedReviewCursorResponse {

    private final List<ReviewListResponse> reviewList;
    private final Long nextCursorReviewId;
    private final LocalDateTime nextCursorCreatedAt;
    private final Boolean hasNext;
}
