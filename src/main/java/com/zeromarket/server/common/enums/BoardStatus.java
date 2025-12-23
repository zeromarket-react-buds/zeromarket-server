package com.zeromarket.server.common.enums;

public enum BoardStatus {
    /** 공지사항 */
    NOTICE("공지사항"),

    /** QNA */
    QNA("묻고답하기"),

    /** 자유게시판 */
    FREE("자유게시판");

    // 필드 추가 (선택 사항: 문자열 설명)
    private final String description;

    // 생성자
    BoardStatus(String description) {
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