package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductCreateRequest;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.dto.WishCountResponse;
import com.zeromarket.server.api.service.ProductCommandService;
import com.zeromarket.server.api.service.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "상품 관련 API")
public class ProductRestController {

    private ProductQueryService productQueryService;
    private ProductCommandService productCommandService;

    @Operation(summary = "상품 목록 조회", description = "검색 포함 상품 목록 조회")
    @GetMapping
    public ResponseEntity<LoadMoreResponse<ProductQueryResponse>> getProductList(@ModelAttribute ProductQueryRequest productQueryRequest) {
       LoadMoreResponse<ProductQueryResponse> result = productQueryService.selectProductList(productQueryRequest);
       return ResponseEntity.ok(result);
    }
    
    //상품 상세 조회
    @Operation(summary = "상품 상세조회", description = "상세조회 화면 - 상품id로 개별조회 + 조회수 증가 + 찜 수 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long productId){
        //조회수 증가 전 상품 존재여부 확인
        ProductDetailResponse result = productQueryService.selectProductDetail(productId);
        //미조회시 404 응답 보내기
        if(result == null){
            return ResponseEntity.notFound().build();
        }
        //조회수 증가
        productQueryService.increaseViewCount(productId);

        return ResponseEntity.ok(result);
    }

    //상품 등록
    @Operation(summary = "상품 등록", description = "상품 정보 + 이미지 업로드")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProduct(
        //@RequestPart : 아래처럼 작성하여, JSON + 파일을 각각 분리하여 받을수있게함
        @RequestPart("data") ProductCreateRequest request,
        // required = false : 스프링이 특정 요청 파트를 반드시 필요로 하지 않게 만드는 옵션,이미지가 없어도 에러를 내지 말고 images = null 로 (상품등록을)처리하라는 의미.
        @RequestPart(value = "images",required = false) List<MultipartFile> images
    )
    {
        Long newProductId = productCommandService.createProduct(request,images);
        //HttpStatus.CREATED = 201
        //메서드 체이닝 - HTTP 201 Created 상태로 newProductId 를 body 에 담아 JSON 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(newProductId);
    }

    //비슷한 상품 조회
    @Operation(summary = "비슷한 상품 조회", description = "현 상품과 비슷한 상품 조회")
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductQueryResponse>> getSimilarProducts(@PathVariable Long productId) {

        return ResponseEntity.ok(productQueryService.findSimilarProducts(productId));
    }


    //상품 수정
    
    //상품 삭제



}
