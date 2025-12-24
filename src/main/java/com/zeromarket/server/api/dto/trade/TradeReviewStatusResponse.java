package com.zeromarket.server.api.dto.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeReviewStatusResponse {

    // 현재 로그인한 유저 기준
    private boolean myReviewExists;       // 내가 쓴 리뷰가 있는지
    private boolean partnerReviewExists;  // 상대가 나에 대한 리뷰가 있는지

    private Long myReviewId;              // 내가 쓴 리뷰 아이디
    private Long partnerReviewId;         // 상대가 쓴 리뷰 아이디

    private Long partnerRating;           // 상대가 점수 매긴거

}