package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQueryRequest {
    private Long offset;
    private Integer size;
    private String keyword;
    private String sort;
    private Long categoryId;
    private Integer minPrice;
    private Integer maxPrice;
    private String area;
}
