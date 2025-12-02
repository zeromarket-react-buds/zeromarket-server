package com.zeromarket.server.api.dto;


import com.zeromarket.server.common.enums.ProductStatus;
import com.zeromarket.server.common.enums.SalesStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailResponse {

    private ProductDetailSellerInfo seller;
    private List<ProductDetailImageInfo> images;
//    private Integer mainImageIndex;

    private Long sellerId;
    private Long productId;
    private String productTitle;
    private String productDescription;
    private Long sellPrice;

    private String categoryDepth1;
    private String categoryDepth2;
    private String categoryDepth3;
    private Long level1Id;
    private Long level2Id;
    private Long level3Id;

    private SalesStatus salesStatus;
    private ProductStatus productStatus;

    private boolean isWished;
    private int wishCount;

    private int viewCount;
    private boolean isDelivery;
    private boolean isDirect;
    private String sellingArea;
    private LocalDateTime createdAt;
    private boolean isHidden;
    private boolean isDeleted;

}
