//찜상태 변경: 찜 등록, 해제, soft delete 적용/복원, 찜토글 기능
package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.mapper.mypage.WishMapper;
import com.zeromarket.server.common.entity.Wish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishCommandServiceImpl implements WishCommandService {

    private final WishMapper wishMapper;

    //  POST /wish → 찜 등록 전용
    @Override
    //public boolean toggleWish(Long memberId, Long productId)
    public boolean addWish(Long memberId, Long productId) {

        Wish existing = wishMapper.findWish(memberId, productId);

        // 1️. 존재하지 않으면 Insert → 찜 생성
        if (existing == null) {
            Wish w = Wish.builder()
                .memberId(memberId)
                .productId(productId)
                .isDeleted(false)   // 찜 상태로 새로 생성
                .build();
            wishMapper.insertWish(w);
            return true; // 찜됨
        }

//        // 2️. soft delete 상태면 → 복원
//        if (Boolean.TRUE.equals(existing.getIsDeleted())) {   // null-safe
//            wishMapper.restoreWish(existing.getWishId());
//            return true;
//        }
//
//        // 3️. 현재 찜 상태면 → soft delete
//        wishMapper.softDeleteWish(existing.getWishId());
//        return false;

        if (existing.getIsDeleted()) {
            wishMapper.restoreWish(existing.getWishId());
            return true;
        }

        return true; // 이미 찜된 상태여도 true 유지
    }

    // DELETE /wish → 찜 해제 전용
    @Override
    public boolean deleteWish(Long memberId, Long productId) {

        Wish wish = wishMapper.findWish(memberId, productId);

        if (wish == null) {
            return false;  // 이미 찜 안 된 상태
        }

        wishMapper.softDeleteWish(wish.getWishId());
        return false;//  찜 해제 → 항상 false 반환
    }

    // 선택적으로 유지: 찜 목록 페이지에서는 toggle이 유용할 수 있음
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

        if (Boolean.TRUE.equals(existing.getIsDeleted())) {
            wishMapper.restoreWish(existing.getWishId());
            return true;
        }

        wishMapper.softDeleteWish(existing.getWishId());
        return false;
    }
}
