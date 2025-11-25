package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;

public interface ProductQueryService {
    LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest productQueryRequest);

    ProductDetailResponse selectProductDetail(Long productId);
}
