package com.zeromarket.server.api.dto.mypage;

import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishProductResponse {

    private Long productId;
    private String productTitle;
    private Long sellPrice;

    // 상품 판매 상태: FOR_SALE, RESERVED, SOLD_OUT
    private String salesStatus;
    private String salesStatusKr; // 판매중, 예약중, 거래완료

    private String productStatus;

    private String thumbnailUrl;
    // UI 박스 밖에서 날짜 그룹핑할 때 사용 (찜한 날짜)
    private LocalDateTime wishCreatedAt;

    // UI 박스 내 "몇 분 전 / 몇 초 전" 표시할 때 사용 (상품 등록 날짜)
    private LocalDateTime productCreatedAt;

    // 거래 방식 표시용
    private Boolean direct;   // 직거래 거래
    private Boolean delivery; // 택배 거래

 //   private TradeType tradeType; //enums_Trade Type 직거래/택배거래
//private TradeType direct;   
//private TradeType delivery; //이 둘도 비권장
//Enum은 상품의 거래방식이 하나만 선택되는 구조일 때만 맞다고함

    //  프론트에서 표시할 문자열
    private String tradeTypeDisplay; // ex) "직거래 · 택배거래" or "직거래" or "택배"
}
