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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class WishRestController {

    //생성자 주입 (Lombok @RequiredArgsConstructor 활용)
    private final WishCommandService wishCommandService;
    private final WishQueryService wishQueryService;

    //로그인 기능 미구현 → 임시 사용자 ID 고정
    private static final Long TEMP_MEMBER_ID = 1L;

    // ⭐ 찜 토글 API (POST)
    // URL: POST /api/products/{productId}/wish
    // 기능: 찜 상태가 없으면 INSERT, 있으면 DELETE → boolean 반환
    @PostMapping("/{productId}/wish")
    public boolean toggleWish(@PathVariable Long productId) {
        return wishCommandService.toggleWish(TEMP_MEMBER_ID, productId);
    }

    // ⭐ 찜 삭제 전용 API (DELETE)
    // ⭐ DELETE → toggleWish 금지 → deleteWish 전용 메서드 사용
    // URL: DELETE /api/products/{productId}/wish
    // 기능: 찜을 강제로 삭제하는 전용 메서드
    // toggle 방식이 아님 → deleteWish 서비스 사용
    @DeleteMapping("/{productId}/wish")
    public boolean removeWish(@PathVariable Long productId) {
        return wishCommandService.deleteWish(TEMP_MEMBER_ID, productId);
    }

    // ⭐ 특정 상품이 찜 되어 있는지 여부 조회 (GET)
    // URL: GET /api/products/{productId}/wish
    // 기능: true(찜 O) / false(찜 X)
    @GetMapping("/{productId}/wish")
    public boolean isWish(@PathVariable Long productId) {
        return wishQueryService.isWished(TEMP_MEMBER_ID, productId);
    }

    // ⭐ 나의 찜한 상품 목록 조회 API (페이징 지원)
    // URL: GET /api/products/wishlist?page=1&size=20
    //
    // 1) wish 테이블에서 현재 유저가 찜한 productId 목록 가져옴
    // 2) 각 productId별로 상품 요약 정보 조회 → WishProductResponse 생성
    // 3) 리스트로 묶어서 프론트로 반환
    @GetMapping("/wishlist")
    public ResponseEntity<List<WishProductResponse>> getMyWishlist(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        // 1) 찜한 상품의 ID 목록 가져오기
        List<Long> ids = wishQueryService.getWishProductIds(TEMP_MEMBER_ID, page, size);

        // 2) 각 상품 ID에 대해 요약 정보 조회 + null 방지 filter
        List<WishProductResponse> result = ids.stream()
            .map(productId -> wishQueryService.selectProductSummary(TEMP_MEMBER_ID, productId))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 3) 최종 응답 반환
        return ResponseEntity.ok(result);
    }
        // ⭐ 유저의 전체 찜 개수 조회 API
        // URL: GET /api/products/wishlist/count
        // 기능: 유저가 찜한 상품 총 개수 반환
        // → MyPage 상단에서 "찜 n개" 표시용
        @GetMapping("/wishlist/count")
        public int getWishCount() {
            return wishQueryService.getWishCount(TEMP_MEMBER_ID);
    }
}


