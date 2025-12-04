package com.zeromarket.server.api.service.mypage;

public interface WishCommandService {
    boolean toggleWish(Long memberId, Long productId);

    // 🔥 삭제 전용 함수 추가
    boolean deleteWish(Long memberId, Long productId);
}
