package com.zeromarket.server.api.dto;

import com.zeromarket.server.common.enums.SalesStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishProductResponse {
    private Long productId;
    private String productTitle;
    private Long sellPrice;
    private SalesStatus salesStatus;     // ← ENUM
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    //위시에 들어간 created_at을 담을 변수
    private LocalDateTime wishCreatedAt;
}