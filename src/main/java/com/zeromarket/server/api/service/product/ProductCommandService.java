package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;

public interface ProductCommandService {

    Long createProduct(ProductCreateRequest request);

    void updateHidden(Long productId, boolean hidden);

    void deleteProduct(Long productId);
}
