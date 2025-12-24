// 자주쓰는 문구 수정
package com.zeromarket.server.api.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCustomTextUpdateRequest {
    private String text;
}
