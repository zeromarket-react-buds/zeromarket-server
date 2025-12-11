package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.WishToggleResponse;

public interface WishSellerService {

    WishToggleResponse toggleSellerLike(Long memberId, Long sellerId);
}
