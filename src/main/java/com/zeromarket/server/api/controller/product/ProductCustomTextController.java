package com.zeromarket.server.api.controller.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextCreateRequest;
import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.product.ProductCustomTextCommandService;
import com.zeromarket.server.api.service.product.ProductCustomTextQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/custom-texts")
public class ProductCustomTextController {

    private final ProductCustomTextQueryService queryService;//목록불러오기
    private final ProductCustomTextCommandService commandService;//문구 등록

    //목록 불러오기
    @GetMapping
    public ResponseEntity<List<ProductCustomTextResponse>> getProductCustomTexts(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                queryService.getProductCustomTexts(user.getMemberId())
        );
    }

    //문구등록
    @PostMapping
    public ResponseEntity<Void> createProductCustomText(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ProductCustomTextCreateRequest request
    ) {
        commandService.createProductCustomText(
                user.getMemberId(),
                request.getText()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
