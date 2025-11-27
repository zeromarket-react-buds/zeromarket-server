package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishCountResponse {
    private Long productId;
    private int wishCount;

}
