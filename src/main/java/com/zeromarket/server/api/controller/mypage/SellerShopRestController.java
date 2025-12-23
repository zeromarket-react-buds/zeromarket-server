package com.zeromarket.server.api.controller.mypage;

import com.zeromarket.server.api.dto.mypage.ReceivedReviewCursorResponse;
import com.zeromarket.server.api.dto.mypage.ReceivedReviewSummaryResponse;
import com.zeromarket.server.api.dto.mypage.SalesProductCursorResponse;
import com.zeromarket.server.api.dto.mypage.SalesProductRequest;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.mypage.ReviewService;
import com.zeromarket.server.api.service.mypage.SellerShopService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SellerShopRestController {

    private final SellerShopService sellerShopService;
    private final ReviewService reviewService;

    @GetMapping("/api/sellers/{sellerId}/products")
    public ResponseEntity<SalesProductCursorResponse> getSalesProductsBySellerCursor(
        @PathVariable Long sellerId,
        @RequestParam(required = false) Long cursorProductId,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "false") boolean includeHidden,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SalesProductRequest req = new SalesProductRequest();
        req.setSellerId(sellerId);
        req.setCursorProductId(cursorProductId);
        req.setCursorCreatedAt(cursorCreatedAt);
        req.setSize(size);
        req.setIncludeHidden(includeHidden);
        req.setLoginMemberId(userDetails != null ? userDetails.getMemberId() : null);

        SalesProductCursorResponse res = sellerShopService.getProductsBySellerCursor(req);

        return ResponseEntity.ok(res);
    }
}
