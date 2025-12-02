package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.WishProductResponse;
import com.zeromarket.server.common.entity.Wish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishMapper {

    Wish findWish(@Param("memberId") Long memberId,
        @Param("productId") Long productId);

    int insertWish(Wish wish);

    int restoreWish(@Param("wishId") Long wishId);

    int softDeleteWish(@Param("wishId") Long wishId);

    // 특정 상품 찜 여부 조회
    boolean isWished(@Param("memberId") Long memberId,
        @Param("productId") Long productId);

    // 내 찜 목록 조회
    List<Long> findWishProductIds(@Param("memberId") Long memberId,
        @Param("offset") Integer offset,
        @Param("size") Integer size);

    // 찜 목록용 상품 요약 조회 추가
    WishProductResponse selectProductSummary(Long productId);
}