package com.zeromarket.server.api.dto.product;

import com.zeromarket.server.common.enums.SalesStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQueryResponse {

    private Long productId;
    private String productTitle;
    private Long sellPrice;
    private String sellingArea;

    private SalesStatus salesStatus;
    private int viewCount;

    private boolean isDelivery;
    private boolean isDirect;

    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private int wishCount;
    //찜목록에 넣기
    private boolean isWished;

    private Long sellerId;//자기상품 찜 방지용

    private Double latitude;
    private Double longitude;
    private String category;
}
