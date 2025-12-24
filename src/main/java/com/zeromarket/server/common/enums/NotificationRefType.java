package com.zeromarket.server.common.enums;

public enum NotificationRefType {
    CHAT_ROOM("CHAT_ROOM"),
    TRADE("TRADE"),
    ORDER("ORDER"),
    PRODUCT("PRODUCT"),
    MEMBER("MEMBER"),
    NONE("NONE");


    private final String description;

    // 생성자
    NotificationRefType(String description) {
        this.description = description;
    }

    // Getter
    public String getDescription() {
        return description;
    }

    public String getName() {
        return this.name();
    }
}
