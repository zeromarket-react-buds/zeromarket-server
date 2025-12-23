package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.WishProductResponse;
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

    boolean isWished(@Param("memberId") Long memberId,
        @Param("productId") Long productId);

    List<Long> findWishProductIds(@Param("memberId") Long memberId,
        @Param("offset") Integer offset,
        @Param("size") Integer size);

    //  찜 상품 요약 조회 (memberId 조건 추가됨)
    WishProductResponse selectProductSummary(
        @Param("memberId") Long memberId,
        @Param("productId") Long productId
    );

    // 추가됨: 찜 개수 조회
    int countWish(@Param("memberId") Long memberId);
}
