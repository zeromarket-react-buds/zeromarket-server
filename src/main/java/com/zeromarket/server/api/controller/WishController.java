package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.TradeHistoryResponse;
import com.zeromarket.server.api.dto.WishCountResponse;
import com.zeromarket.server.api.dto.WishProductResponse;
import com.zeromarket.server.api.service.WishCommandService;
import com.zeromarket.server.api.service.WishQueryService;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zeromarket.server.api.service.ProductQueryService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class WishController {

    private final WishCommandService wishCommandService;
    private final WishQueryService wishQueryService;
    private final ProductQueryService productQueryService;
    // 여기 컨트롤러의 selectProductSummary, ProductQueryServiceImpl 클래스roductQueryMapper를 지역변수로

    private static final Long TEMP_MEMBER_ID = 1L; // 테스트용

    // 찜 추가/토글
    @PostMapping("/{productId}/favorite")
    public boolean toggleFavorite(@PathVariable Long productId) {
        return wishCommandService.toggleWish(TEMP_MEMBER_ID, productId);
    }

    // 찜 해제
    @DeleteMapping("/{productId}/favorite")
    public boolean removeFavorite(@PathVariable Long productId) {
        return wishCommandService.toggleWish(TEMP_MEMBER_ID, productId);
    }

    // 찜 여부 조회
    @GetMapping("/{productId}/favorite")
    public boolean isFavorite(@PathVariable Long productId) {
        return wishQueryService.isWished(TEMP_MEMBER_ID, productId);
    }
    // ⭐ 찜 목록 조회 테스트
//    @GetMapping("/wishlist")
//    public List<Map<String, Object>> getMyWishlist() {
//
//        List<Map<String, Object>> list = new ArrayList<>();
//
//        Map<String, Object> item = new HashMap<>();
//        item.put("productId", 1);
//        item.put("productTitle", "테스트 상품");
//        item.put("sellPrice", 20000);
//        item.put("thumbnailUrl", "");
//        item.put("time", "직거래 · 1일 전");
//        item.put("date", "2025.12.01");
//        item.put("salesStatus", Map.of("description", "판매중"));
//
//        list.add(item);
//
//        return list;
//    }
    @GetMapping("/wishlist")
    //responseEntity를 넣은 구조. List<Dtoresponse>를 한번 responseEntity가 감쌈
    public ResponseEntity<List<WishProductResponse>> getMyWishlist(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = TEMP_MEMBER_ID;

        // 1) 찜한 product_id 리스트 조회
        List<Long> productIds = wishQueryService.getWishProductIds(memberId, page, size);

        // 2) 각 productId로 상품 요약 정보 조회
        List<WishProductResponse> result = productIds.stream()
            .map(wishQueryService::selectProductSummary)
            .filter(Objects::nonNull)  // 프론트가 null 때문에 깨지는 걸 완벽히 막아주는 안전장치
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);

        //responseEntity를 안했을때 return값 처리
//        return productIds.stream()
//            .map(productQueryService::selectProductSummary)
//            .filter(Objects::nonNull)  // 프론트가 null 때문에 깨지는 걸 완벽히 막아주는 안전장치
//            .collect(Collectors.toList());
    }
}

