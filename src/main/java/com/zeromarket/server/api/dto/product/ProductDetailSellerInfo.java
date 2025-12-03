package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailSellerInfo {
    private Long sellerId;              // seller_id
    private String sellerNickName;      // seller_nickname
    private String sellerIntroduction;  // seller_introduction


}
