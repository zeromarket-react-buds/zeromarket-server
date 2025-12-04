package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.WishProductResponse;
import com.zeromarket.server.api.service.mypage.WishCommandService;
import com.zeromarket.server.api.service.mypage.WishQueryService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zeromarket.server.api.service.product.ProductQueryService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class WishController {

    private final WishCommandService wishCommandService;
    private final WishQueryService wishQueryService;

    private static final Long TEMP_MEMBER_ID = 1L;

    @PostMapping("/{productId}/wish")
    public boolean toggleWish(@PathVariable Long productId) {
        return wishCommandService.toggleWish(TEMP_MEMBER_ID, productId);
    }

//    @DeleteMapping("/{productId}/wish")
//    public boolean removeWish(@PathVariable Long productId) {
//        return wishCommandService.toggleWish(TEMP_MEMBER_ID, productId);
//    }

    // ⭐ DELETE → toggleWish 금지 → deleteWish 전용 메서드 사용
    @DeleteMapping("/{productId}/wish")
    public boolean removeWish(@PathVariable Long productId) {
        return wishCommandService.deleteWish(TEMP_MEMBER_ID, productId);
    }

    @GetMapping("/{productId}/wish")
    public boolean isWish(@PathVariable Long productId) {
        return wishQueryService.isWished(TEMP_MEMBER_ID, productId);
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<WishProductResponse>> getMyWishlist(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        List<Long> ids = wishQueryService.getWishProductIds(TEMP_MEMBER_ID, page, size);

        List<WishProductResponse> result = ids.stream()
            .map(productId -> wishQueryService.selectProductSummary(TEMP_MEMBER_ID, productId))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
        // ⭐ 추가됨: 찜 개수 조회 API
        @GetMapping("/wishlist/count")
        public int getWishCount() {
            return wishQueryService.getWishCount(TEMP_MEMBER_ID);
    }
}


