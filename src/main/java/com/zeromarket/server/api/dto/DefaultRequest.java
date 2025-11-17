package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultRequest {

    private int currentPage = 1;         // 요청 페이지 번호 (기본값 1)
    private int pageSize = 10;    // 페이지당 항목 수 (기본값 10)

    public int getOffset() {
        // OFFSET = (페이지 번호 - 1) * 페이지 크기
        // page가 1일 때 OFFSET은 0이 되어야 합니다.
        return (currentPage - 1) * pageSize;
    }
    private String searchType;    // 검색 필드 유형 (예: "TITLE", "WRITER", "CONTENT")
    private String searchKeyword; // 실제 검색어
}
