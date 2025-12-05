package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductCommandMapper {

    void insertProduct(ProductCreateRequest request);

    void insertProductImage(

        @Param("productId") Long productId,
        @Param("imageUrl") String imageUrl,
        @Param("sortOrder") Integer sortOrder,
        @Param("isMain") Boolean isMain
        );

    void updateHidden(Long productId, boolean hidden);

    void softDeleteProduct(Long productId);

    void updateProduct(@Param("productId") Long productId, @Param("request") ProductUpdateRequest request);

    void deleteImagesByProductId(Long productId);

    //상품 판매자id조회 메서드
    Long getProductSellerId(Long productId);
}
