package com.zeromarket.server.api.controller.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.product.ProductCustomTextQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/custom-texts")
public class ProductCustomTextController {

    private final ProductCustomTextQueryService queryService;

    @GetMapping
    public ResponseEntity<List<ProductCustomTextResponse>> getProductCustomTexts(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                queryService.getProductCustomTexts(user.getMemberId())
        );
    }
}
