package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.service.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "상품 관련 API")
public class ProductRestController {

    private ProductQueryService productQueryService;

    @Operation(summary = "상품 목록 조회", description = "검색 포함 상품 목록 조회")
    @GetMapping
    public ResponseEntity<LoadMoreResponse<ProductQueryResponse>> getProductList(@ModelAttribute ProductQueryRequest productQueryRequest) {
       LoadMoreResponse<ProductQueryResponse> result = productQueryService.selectProductList(productQueryRequest);
       return ResponseEntity.ok(result);
    }

}
