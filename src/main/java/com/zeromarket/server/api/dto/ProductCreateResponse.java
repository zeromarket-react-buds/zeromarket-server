package com.zeromarket.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductCreateResponse {
    private Long productId;
    private String message; //결과상태표시

}
