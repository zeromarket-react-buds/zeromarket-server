package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.dto.WishCountResponse;
import java.util.List;

public interface ProductQueryService {
    LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest productQueryRequest);

    //상품상세 통합 메서드
    ProductDetailResponse getProductDetail(Long productId);

    List<ProductQueryResponse> findSimilarProducts(Long productId);

}
