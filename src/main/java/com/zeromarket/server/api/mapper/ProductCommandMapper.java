package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.ProductCreateRequest;
import javax.swing.SortOrder;
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

}
