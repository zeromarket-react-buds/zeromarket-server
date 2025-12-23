package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductAiDraftRequest {
    private String caption;
    private List<String> tags;

    private Long sellPrice;
}

