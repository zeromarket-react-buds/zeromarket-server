package com.zeromarket.server.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TradeType {
    /** DELIVERY 택배거래 */
    DELIVERY("택배거래"),

    /** DIRECT 직거래 */
    DIRECT("직거래");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    TradeType(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }
}
