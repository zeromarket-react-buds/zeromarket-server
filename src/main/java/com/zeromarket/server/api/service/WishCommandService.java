package com.zeromarket.server.api.service;

public interface WishCommandService {
    boolean toggleWish(Long memberId, Long productId);
}
