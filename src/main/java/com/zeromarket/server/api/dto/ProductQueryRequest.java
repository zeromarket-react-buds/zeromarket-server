package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQueryRequest {
    private Long cursor;
    private Integer size;
    private Long level3Id;
    private String keyword;
    private String sort;
}
