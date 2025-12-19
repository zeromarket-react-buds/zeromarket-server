package com.zeromarket.server.api.service.order;

import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;

/**
 * <상태 전이 Validation>
 * PAID → DELIVERY_READY → SHIPPED → DELIVERED
 * PAID / DELIVERY_READY → CANCELED
 *
 * - 서비스에서 사용
 * - Controller에서 상태 안 믿는다. 항상 서버에서 검증
 */

public class OrderStatusValidator {

    public static void validateTransition(String current, String next) {
        if (current.equals("PAID") &&
            (next.equals("DELIVERY_READY") || next.equals("CANCELED"))) return;

        if (current.equals("DELIVERY_READY") &&
            (next.equals("SHIPPED") || next.equals("CANCELED"))) return;

        if (current.equals("SHIPPED") && next.equals("DELIVERED")) return;

        throw new IllegalStateException(
            "Invalid order status transition: " + current + " -> " + next
        );
    }
}
