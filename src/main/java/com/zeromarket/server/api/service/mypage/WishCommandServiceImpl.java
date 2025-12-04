package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.mapper.mypage.WishMapper;
import com.zeromarket.server.common.entity.Wish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishCommandServiceImpl implements WishCommandService {

    private final WishMapper wishMapper;

    @Override
    public boolean toggleWish(Long memberId, Long productId) {

        Wish existing = wishMapper.findWish(memberId, productId);

        // 1️⃣ 존재하지 않으면 Insert → 찜 생성
        if (existing == null) {
            Wish w = Wish.builder()
                .memberId(memberId)
                .productId(productId)
                .isDeleted(false)   // ⭐ 찜 상태로 새로 생성
                .build();
            wishMapper.insertWish(w);
            return true; // 찜됨
        }

        // 2️⃣ soft delete 상태면 → 복원
        if (Boolean.TRUE.equals(existing.getIsDeleted())) {   // ⭐ null-safe
            wishMapper.restoreWish(existing.getWishId());
            return true;
        }

        // 3️⃣ 현재 찜 상태면 → soft delete
        wishMapper.softDeleteWish(existing.getWishId());
        return false;
    }

    @Override
    public boolean deleteWish(Long memberId, Long productId) {

        Wish wish = wishMapper.findWish(memberId, productId);

        if (wish == null) {
            return false; // 이미 삭제된 상태
        }

        wishMapper.softDeleteWish(wish.getWishId());
        return true;
    }
}
