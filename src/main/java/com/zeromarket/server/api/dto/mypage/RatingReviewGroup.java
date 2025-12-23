package com.zeromarket.server.api.dto.mypage;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingReviewGroup {
    private int rating;
    private int totalCount; // 총 개수(3개 제한 X)
    private List<ReceivedReviewSummaryDto> latestReviews;

    public RatingReviewGroup(int rating, int totalCount, List<ReceivedReviewSummaryDto> latestReviews) {
        this.rating = rating;
        this.totalCount = totalCount;
        this.latestReviews = latestReviews;
    }
}
