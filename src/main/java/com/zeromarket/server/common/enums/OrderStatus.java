package com.zeromarket.server.common.enums;

public enum OrderStatus {
    PAID,
    DELIVERY_READY,
    SHIPPED,
    DELIVERED,
    CANCELED;

//    public boolean canTransitTo(OrderStatus next) {
//        return switch (this) {
//            case PAID -> next == DELIVERY_READY || next == CANCELED;
//            case DELIVERY_READY -> next == SHIPPED || next == CANCELED;
//            case SHIPPED -> next == DELIVERED;
//            case DELIVERED, CANCELED -> false;
//        };
//    }
}
