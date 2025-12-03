package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductImageCreateRequest {
    private Long productId;
    private String imageUrl;

}
