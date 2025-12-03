package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductQueryRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductQueryMapper {

    List<ProductQueryResponse> selectProductsOffset(ProductQueryRequest queryReq);

    ProductDetailResponse selectProductDetail(Long productId);

    void updateViewCount(Long productId);

    List<ProductQueryResponse> selectSimilarProducts(Long productId);

    ProductBasicInfo selectBasicInfo(Long productId);
}
