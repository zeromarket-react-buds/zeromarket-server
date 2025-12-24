package com.zeromarket.server.api.mapper.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductCustomTextMapper {
    //자주 쓰는 문구 목록 불러오기
    List<ProductCustomTextResponse> findProductCustomTexts(
            @Param("memberId") Long memberId,
            @Param("contentType") String contentType
    );
    //등록
    void insertProductCustomText(
            @Param("memberId") Long memberId,
            @Param("contentType") String contentType,
            @Param("text") String text
    );
    //삭제
    void softDeleteProductCustomText(@Param("id") Long id);

    //수정
    void updateProductCustomText(
            @Param("id") Long id,
            @Param("text") String text
    );
}
