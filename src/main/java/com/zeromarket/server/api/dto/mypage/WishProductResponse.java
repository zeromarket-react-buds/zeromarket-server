package com.zeromarket.server.api.dto.mypage;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishProductResponse {

    private Long productId;
    private String productTitle;
    private Long sellPrice;

    // 🔥 상품 판매 상태: FOR_SALE, RESERVED, SOLD_OUT
    private String salesStatus;
    private String salesStatusKr; // 판매중, 예약중, 거래완료

    private String productStatus;

    private String thumbnailUrl;
    private LocalDateTime createdAt; // 찜한 날짜

    // 🔥 거래 방식 표시용
    private Boolean direct;   // 직거래 가능?
    private Boolean delivery; // 택배 거래 가능?

    // 🔥 프론트에서 표시할 문자열
    private String tradeTypeDisplay; // ex) "직거래 · 택배거래" or "직거래" or "택배"
}
