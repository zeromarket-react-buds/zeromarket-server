package com.zeromarket.server.api.service.mypage;

public interface WishCommandService {
    boolean toggleWish(Long memberId, Long productId);
}
