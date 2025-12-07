package com.zeromarket.server.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TradeStatus {
    /** PENDING 예약중 */
    PENDING("예약중"),

    /** COMPLETED 거래완료 */
    COMPLETED("거래완료"),

    /** CANCELED 취소 */
    CANCELED("취소");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    TradeStatus(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }
}

