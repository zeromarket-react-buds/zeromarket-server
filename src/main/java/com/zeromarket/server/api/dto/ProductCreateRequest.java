package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequest {
    private Long sellerId;
    private String productTitle;
    private Long categoryDepth1;
    private Long categoryDepth2;
    private Long categoryDepth3;
    private Long sellPrice;
    private String productDescription;
    private String productStatus;
    private String salesStatus;
    private boolean direct;
    private boolean delivery;
    private String sellingArea;

    private Long productId; //자동생성값
    public Long getProductId() { //DB insert 후 자동생성 id 받을때
        return productId;
    }
}
