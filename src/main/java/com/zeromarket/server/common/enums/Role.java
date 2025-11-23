package com.zeromarket.server.common.enums;

public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    Role(String description) {
        this.description = description;
    }

    // Getter
    public String getDescription() {
        return description;
    }
}
