package com.zeromarket.server.api.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailResponse {

    private ProductDetailSellerInfo seller;
    private List<ProductDetailImageInfo> images;
    private Integer mainImageIndex;

    private Long sellerId;
    //private String sellerNickname;
    private Long productId;
    private String productTitle;
    private String productDescription;
    private Long sellPrice;

//    private String categoryDepth1;
//    private String categoryDepth2;
    private String categoryDepth3;
    private String productStatus;
    private String salesStatus;
    private int viewCount;
    private int wishCount;

    private boolean isDelivery;
    private boolean isDirect;
    private String sellingArea;
    private LocalDateTime createdAt;
    private boolean isHidden;
    private boolean isDeleted;


}
