package com.zeromarket.server.api.service;

import com.zeromarket.server.api.mapper.WishMapper;
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

        if (existing == null) {
            Wish w = Wish.builder()
                .memberId(memberId)
                .productId(productId)
                .isDeleted(false)
                .build();
            wishMapper.insertWish(w);
            return true;
        }

        if (existing.getIsDeleted()) {
            wishMapper.restoreWish(existing.getWishId());
            return true;
        }

        wishMapper.softDeleteWish(existing.getWishId());
        return false;
    }
}
