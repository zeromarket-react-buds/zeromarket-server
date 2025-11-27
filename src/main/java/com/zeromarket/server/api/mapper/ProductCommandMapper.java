package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.ProductCreateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCommandMapper {

    void insertProduct(ProductCreateRequest request);

    void insertProductImage(Long newProductId, String imageUrl);
}
