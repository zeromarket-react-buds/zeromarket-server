package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;
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
}
