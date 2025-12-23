package com.zeromarket.server.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStatus {
    /* PAID 결제완료 */
    PAID("결제완료"),

    /* DELIVERY_READY 주문확인 */
    DELIVERY_READY("주문확인"),

    /* SHIPPED 배송중 */
    SHIPPED("배송중"),

    /* DELIVERED 배송완료 */
    DELIVERED("배송완료"),

    /* CANCELED 취소 */
    CANCELED("취소");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    OrderStatus(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }


//    public boolean canTransitTo(OrderStatus next) {
//        return switch (this) {
//            case PAID -> next == DELIVERY_READY || next == CANCELED;
//            case DELIVERY_READY -> next == SHIPPED || next == CANCELED;
//            case SHIPPED -> next == DELIVERED;
//            case DELIVERED, CANCELED -> false;
//        };
//    }
}
