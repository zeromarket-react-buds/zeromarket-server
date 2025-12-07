package com.zeromarket.server.api.dto.product;

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
    private Long minPrice;
    private Long maxPrice;
    private String area;
    private Long memberId;// 찜용
}
