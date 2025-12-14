package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.LikedSellerResponse;
import com.zeromarket.server.api.dto.mypage.WishToggleResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.mypage.WishSellerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WishSellerController {

    private final WishSellerService wishSellerService;

    /**
     * 셀러샵 좋아요 토글
     * @param user
     * @param sellerId
     * @return
     */
    @PostMapping("/api/sellershop/{sellerId}/like")
    public ResponseEntity<WishToggleResponse> toggleSellerShopLike(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long sellerId
    ) {
        WishToggleResponse response = wishSellerService.toggleSellerLike(
            user.getMemberId(),
            sellerId
        );
        return ResponseEntity.ok(response);
    }

    // 셀러 찜 목록 조회 기능
    @GetMapping("/api/me/wishlist/sellers")
    public ResponseEntity<List<LikedSellerResponse>> getLikedSellers(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
            wishSellerService.getLikedSellers(user.getMemberId())
        );
    }

}
