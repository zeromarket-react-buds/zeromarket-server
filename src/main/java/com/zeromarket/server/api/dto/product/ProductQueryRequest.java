package com.zeromarket.server.api.dto.product;

import com.zeromarket.server.common.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<TradeType> trade;
    private Long memberId;// 찜용
    private Double latitude;
    private Double longitude;
    private Double swLat;
    private Double swLng;
    private Double neLat;
    private Double neLng;
}
