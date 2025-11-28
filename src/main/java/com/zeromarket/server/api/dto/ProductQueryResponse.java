package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;
import com.zeromarket.server.common.enums.SalesStatus;
import com.zeromarket.server.common.enums.ProductStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductQueryResponse {

    private Long productId;
    private String productTitle;
    private Long sellPrice;
    private String sellingArea;

    private ProductStatus productStatus;
    private SalesStatus salesStatus;
    private int viewCount;

    private boolean isDelivery;
    private boolean isDirect;

    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private int wishCount;
}
