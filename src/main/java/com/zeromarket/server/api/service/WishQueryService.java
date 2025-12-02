package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.WishProductResponse;
import java.util.List;

public interface WishQueryService {
    boolean isWished(Long memberId, Long productId);
    List<Long> getWishProductIds(Long memberId, Integer page, Integer size);

    // 찜상품목록: 단일 상품 요약 조회
    WishProductResponse selectProductSummary(Long productId);

}
