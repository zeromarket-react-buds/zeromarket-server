package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.SalesProductRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {

    List<ProductQueryResponse> selectProductsBySellerCursor(SalesProductRequest req);
}
