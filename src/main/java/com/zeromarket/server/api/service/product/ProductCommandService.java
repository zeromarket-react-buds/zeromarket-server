package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;
import com.zeromarket.server.api.dto.product.ProductDetailImageInfo;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest;
import java.util.List;

public interface ProductCommandService {

    Long createProduct(ProductCreateRequest request);

    void updateHidden(Long productId, boolean hidden);

//    void updateHidden(Long productId, boolean hidden, Long loggedInUserId);

    void deleteProduct(Long productId);

    void updateProduct(Long productId, ProductUpdateRequest request);

    void validateProductOwnership(Long productId, Long memberId);
}
