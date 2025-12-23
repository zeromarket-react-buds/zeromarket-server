package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.WishProductResponse;
import com.zeromarket.server.api.mapper.mypage.WishMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishQueryServiceImpl implements WishQueryService {

    private final WishMapper wishMapper;

    @Override
    public boolean isWished(Long memberId, Long productId) {
        return wishMapper.isWished(memberId, productId);
    }

    // Long ← 임포트할때 다양하게 있는데 공부
    // int/long: null값을 처리 못함, Integer/Long:null값을 처리할 수 있음
    @Override
    public List<Long> getWishProductIds(Long memberId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return wishMapper.findWishProductIds(memberId, offset, size);
    }

    // ⭐ (memberId 추가)
    @Override
    public WishProductResponse selectProductSummary(Long memberId, Long productId) {

        // 수정: memberId + productId 동시 전달
        WishProductResponse product = wishMapper.selectProductSummary(memberId, productId);

        if (product == null) return null;

        // 🔥 거래완료 상품 제외
        if ("SOLD_OUT".equals(product.getSalesStatus())) return null;

        return product;
    }

    // ⭐ 추가됨: 찜 개수 조회
    @Override
    public int getWishCount(Long memberId) {
        return wishMapper.countWish(memberId);   // ← 수정됨(오타 수정!)
    }
}
