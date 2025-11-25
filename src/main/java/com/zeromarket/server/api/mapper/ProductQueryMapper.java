package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductQueryMapper {

    List<ProductQueryResponse> selectProductsCursor(ProductQueryRequest queryReq);
}
