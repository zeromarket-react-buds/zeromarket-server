package com.zeromarket.server.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateResponse {
    private Long productId;
    private String message; //결과상태표시

}
