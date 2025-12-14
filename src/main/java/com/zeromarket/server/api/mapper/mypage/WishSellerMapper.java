package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.LikedSellerResponse;
import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WishSellerMapper {
//    기존 여부 확인
WishSellerDto selectWishSeller(
    @Param("memberId") Long memberId,
    @Param("sellerId") Long sellerId
);

//    신규 생성
    int insertWishSeller(WishSellerDto wishSellerDto);
//    int insertWishSeller(Long memberId, Long sellerId);

    // 상태 활성화(is_deleted=false)로 변경
    int reactiveWishSeller(@Param("wishSellerId") Long wishSellerId);

//    삭제(is_deleted=true) 처리
    int softDeleteWishSeller(@Param("wishSellerId") Long wishSellerId);

    // 셀러 찜 목록 조회 기능
    List<LikedSellerResponse> selectLikedSellers(
        @Param("memberId") Long memberId
    );
}
