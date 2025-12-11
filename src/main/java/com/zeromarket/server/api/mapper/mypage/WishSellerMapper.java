package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WishSellerMapper {
//    기존 여부 확인
    WishSellerDto selectWishSeller(Long memberId, Long sellerId);

//    신규 생성
    int insertWishSeller(WishSellerDto wishSellerDto);
//    int insertWishSeller(Long memberId, Long sellerId);

//    상태 활성화(is_deleted=true)로 변경
    int reactiveWishSeller(Long wishSellerId);
    
//    삭제(is_deleted=true) 처리
    int softDeleteWishSeller(Long wishSellerId);
}
