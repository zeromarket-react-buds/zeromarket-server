package com.zeromarket.server.api.dto.mypage;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishProductResponse {
    private Long productId;
    private String productTitle;
    private Long sellPrice;
    private String salesStatus;
    private String productStatus;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}