package com.zeromarket.server.api.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailSellerInfo {
    private Long memberId;
    private String nickname;
    private String introduction; // 자기소개
    private String role;
    private LocalDateTime createdAt;

}
