package com.zeromarket.server.api.dto.mypage;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceivedReviewSummaryResponse {
    private RatingReviewGroup rating5;
    private RatingReviewGroup rating4;
//    private int totalCount;
    private String nickname;

}
