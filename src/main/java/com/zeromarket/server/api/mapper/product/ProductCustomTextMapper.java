package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductCustomTextMapper {
    List<ProductCustomTextResponse> findProductCustomTexts(
            @Param("memberId") Long memberId,
            @Param("contentType") String contentType
    );

    void insertProductCustomText(
            @Param("memberId") Long memberId,
            @Param("contentType") String contentType,
            @Param("text") String text
    );
}
