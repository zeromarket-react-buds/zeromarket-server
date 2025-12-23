package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailImageInfo {
    private Long imageId;
    private String imageUrl;
    private boolean isMain;
    private int sortOrder;

}
