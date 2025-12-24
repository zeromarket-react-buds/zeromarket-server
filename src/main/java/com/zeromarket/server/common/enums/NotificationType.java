package com.zeromarket.server.common.enums;

public enum NotificationType {
    CHAT_MESSAGE("CHAT_MESSAGE"),
    TRADE_STATUS("TRADE_STATUS"),
    ORDER_STATUS("ORDER_STATUS"),
    KEYWORD_MATCH("KEYWORD_MATCH"),
    SYSTEM("SYSTEM");

    private final String description;

    // 생성자
    NotificationType(String description) {
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
