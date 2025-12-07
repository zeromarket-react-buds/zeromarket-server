package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailSellerInfo {
    private Long sellerId;              // seller_id
    private String sellerNickName; // seller_nickname
    private String profileImage; // profile_image
    private String sellerIntroduction;  // seller_introduction
    private Double trustScore; // review 테이블의 rating


}
