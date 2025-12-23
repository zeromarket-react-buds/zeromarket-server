package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.LikedSellerResponse;
import com.zeromarket.server.api.dto.mypage.WishSellerDto;
import com.zeromarket.server.api.dto.mypage.WishToggleResponse;
import com.zeromarket.server.api.mapper.mypage.WishSellerMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WishSellerServiceImpl implements WishSellerService {

    private final WishSellerMapper wishMapper;

    @Transactional
    public WishToggleResponse toggleSellerLike(Long memberId, Long sellerId) {

        // 기존 기록 조회
        WishSellerDto wish = wishMapper.selectWishSeller(memberId, sellerId);
        System.out.println(wish);

        // 최초 찜
        if (wish == null) {
            WishSellerDto wishSellerDto = new WishSellerDto();
            wishSellerDto.setMemberId(memberId);
            wishSellerDto.setSellerId(sellerId);

            wishMapper.insertWishSeller(wishSellerDto);
            return new WishToggleResponse(true);  // 좋아요 ON
        }

        // 이미 존재하나 활성화 상태 → 비활성화로 변경
        if (!wish.getIsDeleted()) {
            wishMapper.softDeleteWishSeller(wish.getWishSellerId());
            return new WishToggleResponse(false); // 좋아요 OFF
        }

        // 존재하나 비활성화 상태 → 활성화로 변경
        wishMapper.reactiveWishSeller(wish.getWishSellerId());
        return new WishToggleResponse(true); // 좋아요 ON
    }

    // 셀러 찜 목록 조회 기능
    @Override
    @Transactional(readOnly = true)
    public List<LikedSellerResponse> getLikedSellers(Long memberId) {
        return wishMapper.selectLikedSellers(memberId);
    }
}
