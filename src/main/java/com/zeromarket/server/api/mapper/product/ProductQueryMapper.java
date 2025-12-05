package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductQueryRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductQueryMapper {
    //상품 목록 조회
    List<ProductQueryResponse> selectProductsOffset(ProductQueryRequest queryReq);

    //상품상세조회 찜 여부(isWished)도 함께 조회하기 위해 productId + memberId 두 개의 값을 넘겨야함
    //ProductDetailResponse selectProductDetail(Long productId);
    ProductDetailResponse selectProductDetail(Map<String, Object> params);

    //조회수 증가
    void updateViewCount(Long productId);
    //비슷한 상품 조회
    List<ProductQueryResponse> selectSimilarProducts(Long productId);

    ProductBasicInfo selectBasicInfo(Long productId);
}
