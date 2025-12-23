package com.zeromarket.server.api.dto.product;

import com.zeromarket.server.common.enums.SalesStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBasicInfo {
    private Long productId;
    private Long sellerId;
    private TradeType tradeType;
    private SalesStatus salesStatus;
    private String mainImage;
}
