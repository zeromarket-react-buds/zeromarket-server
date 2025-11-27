package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.dto.WishCountResponse;

public interface ProductQueryService {
    LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest productQueryRequest);

    ProductDetailResponse selectProductDetail(Long productId);

    void increaseViewCount(Long productId);

    WishCountResponse getWishCount(Long productId);
}
