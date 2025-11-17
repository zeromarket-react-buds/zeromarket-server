package com.zeromarket.server.common.enums;

public enum SalesStatus {
    /** 판매 중 (상품 구매 가능) */
    FOR_SALE("판매 중"),

    /** 예약됨 (잠시 판매 대기) */
    RESERVED("예약됨"),

    /** 품절 (재고 없음) */
    SOLD_OUT("품절");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    SalesStatus(String description) {
        this.description = description;
    }

    // Getter
    public String getDescription() {
        return description;
    }
}