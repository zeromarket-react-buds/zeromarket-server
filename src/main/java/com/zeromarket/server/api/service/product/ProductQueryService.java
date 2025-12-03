package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductQueryRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import java.util.List;

public interface ProductQueryService {
    LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest productQueryRequest);

    //상품상세 통합 메서드
    ProductDetailResponse getProductDetail(Long productId);

    List<ProductQueryResponse> findSimilarProducts(Long productId);

}
