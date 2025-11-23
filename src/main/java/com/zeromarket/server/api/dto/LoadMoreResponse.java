package com.zeromarket.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoadMoreResponse<T> {
    private List<T> content;
    private Long cursor;
    private boolean hasNext;

    public static <T> LoadMoreResponse<T> of(List<T> content, Long cursor, boolean hasNext) {
        return new LoadMoreResponse<>(content, cursor, hasNext);
    }
}

