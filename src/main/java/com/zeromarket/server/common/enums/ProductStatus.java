package com.zeromarket.server.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProductStatus {

    /** USED, 중고 (사용감 있음) */
    USED("사용감 있음"),

    /** OPENED_UNUSED, 중고 (개봉 미사용) */
    OPENED_UNUSED("개봉 미사용"),

    /** UNOPENED 새상품 (미개봉) */
    UNOPENED("미개봉");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    ProductStatus(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }
}
