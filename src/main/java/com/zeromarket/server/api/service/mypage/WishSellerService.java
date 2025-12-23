package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.LikedSellerResponse;
import com.zeromarket.server.api.dto.mypage.WishToggleResponse;
import java.util.List;

public interface WishSellerService {

    WishToggleResponse toggleSellerLike(Long memberId, Long sellerId);
    // 셀러 찜 목록 조회 기능
    List<LikedSellerResponse> getLikedSellers(Long memberId);
}
