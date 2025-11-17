package com.zeromarket.server.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {

    // 한 블록에 보여줄 페이지 개수 (예: 5개) < 6 7 8 (9) 10 >  <-- 이 갯수
    private static final int DISPLAY_PAGE_NUM = 5;

    private List<T> content;
    private int totalCount;
    private int pageSize; // 한 페이지에 보여줄 게시글 갯수
    private int currentPage;
    private int totalPages;
    private boolean hasNext;       // 다음 페이지 존재 여부
    private boolean hasPrevious;   // 이전 페이지 존재 여부
    private int startPage;
    private int endPage;

    public PageResponse(List<T> content, int totalCount, int pageSize, int currentPage) {
        this.content = content;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;

        this.totalPages = (int)Math.ceil((double)totalCount/pageSize);

        // endPage 계산: (현재 페이지를 DISPLAY_PAGE_NUM으로 나눈 올림) * DISPLAY_PAGE_NUM
        int tempEndPage = (int) (Math.ceil(currentPage / (double) DISPLAY_PAGE_NUM) * DISPLAY_PAGE_NUM);
        this.startPage = tempEndPage - DISPLAY_PAGE_NUM + 1;
        // endPage가 totalPages를 초과하지 않도록 보정
        this.endPage = Math.min(tempEndPage, this.totalPages);

        this.hasPrevious = this.startPage > 1;
        this.hasNext = this.endPage < this.totalPages;
    }
}