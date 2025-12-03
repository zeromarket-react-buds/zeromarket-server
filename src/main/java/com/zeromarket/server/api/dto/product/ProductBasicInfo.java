package com.zeromarket.server.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBasicInfo {
    private Long productId;
    private Long sellerId;
    private String mainImage;
}
