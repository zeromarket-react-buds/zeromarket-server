package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.*;

import java.util.List;

public interface ProductCommandService {

    Long createProduct(ProductCreateRequest request);

    void updateHidden(Long productId, boolean hidden);

//    void updateHidden(Long productId, boolean hidden, Long loggedInUserId);

    void deleteProduct(Long productId);

    void updateProduct(Long productId, ProductUpdateRequest request);

    void validateProductOwnership(Long productId, Long memberId);

    void createProductLocation(Long newProductId, ProductCreateRequest request, Long memberId);

    ProductVisionResponse productVisionAnalyze(byte[] bytes, String contentType);

    ProductAiDraftResponse generateAiDraft(ProductAiDraftRequest request);
}
