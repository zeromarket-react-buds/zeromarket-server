package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest.LocationDto;
import com.zeromarket.server.common.enums.SalesStatus;
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

    Long getProductSellerId(Long productId);

    int updateProductStatus(@Param("productId") Long productId, @Param("salesStatus") SalesStatus salesStatus);

    void insertProductLocation(
        @Param("productId") Long newProductId,
        @Param("request") ProductCreateRequest request,
        @Param("memberId") Long memberId
    );

    void deleteProductLocation(@Param("productId") Long productId);

    void insertProductLocationFromUpdate(
        @Param("productId") Long productId,
        @Param("request") ProductUpdateRequest request,
        @Param("location") LocationDto location);
}
