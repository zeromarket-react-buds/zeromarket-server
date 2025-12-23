package com.zeromarket.server.common.enums;

public enum UserType {

    BUYER("판매자"),

    SELLER("구매자");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    UserType(String description) {
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
