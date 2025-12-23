package com.zeromarket.server.api.dto.mypage;

import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesProductCursorResponse {
    private List<ProductQueryResponse> items;
    private Long nextCursorProductId;
    private LocalDateTime nextCursorCreatedAt;
    private boolean hasNext;
}
