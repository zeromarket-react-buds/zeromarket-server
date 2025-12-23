package com.zeromarket.server.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCustomTextResponse {
    private Long id;    // custom_text_id
    private String text; // custom_content
}
