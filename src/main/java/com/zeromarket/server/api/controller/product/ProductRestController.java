package com.zeromarket.server.api.controller.product;

import com.zeromarket.server.api.dto.product.HideRequest;
import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.product.ProductCreateRequest;
import com.zeromarket.server.api.dto.product.ProductCreateResponse;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductQueryRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.product.ProductCommandService;
import com.zeromarket.server.api.service.product.ProductQueryService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "상품 관련 API")
public class ProductRestController {

    private ProductQueryService productQueryService;
    private ProductCommandService productCommandService;

//    @Operation(summary = "상품 목록 조회", description = "검색 포함 상품 목록 조회")
//    @GetMapping
//    public ResponseEntity<LoadMoreResponse<ProductQueryResponse>> getProductList(@ModelAttribute ProductQueryRequest productQueryRequest) {
//       LoadMoreResponse<ProductQueryResponse> result = productQueryService.selectProductList(productQueryRequest);
//       return ResponseEntity.ok(result);
//    }
    @Operation(summary = "상품 목록 조회", description = "검색 포함 상품 목록 조회")
    @GetMapping
    public ResponseEntity<LoadMoreResponse<ProductQueryResponse>> getProductList(
        @ModelAttribute ProductQueryRequest productQueryRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // ⭐ 로그인한 사용자라면 memberId 전달
        if (userDetails != null) {
            productQueryRequest.setMemberId(userDetails.getMemberId());
        } else {
            // ⭐ 비로그인 → memberId = 0 (항상 찜 false)
            productQueryRequest.setMemberId(0L);
        }

        LoadMoreResponse<ProductQueryResponse> result =
            productQueryService.selectProductList(productQueryRequest);

        return ResponseEntity.ok(result);
    }

    //상품 상세 조회
    @Operation(summary = "상품 상세조회", description = "상세조회 화면 - 상품id로 개별조회 + 조회수 증가 + 찜 수 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(
        @PathVariable Long productId,
        @RequestParam(required = false) Long memberId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        // 1️⃣ 로그인 사용자 정보가 있으면 memberId 재설정
        if (userDetails != null) {
            memberId = userDetails.getMemberId();
        }

        // 2️⃣ 로그인 상태가 아니면 TEMP_MEMBER_ID 사용 (비로그인 조회 가능)
        if (memberId == null) {
            memberId = 0L; // 비로그인 → 항상 찜해제 상태로 보여줌
        }

        // 임시 로그인/미구현 상태라면 TEMP_MEMBER_ID 사용
        // 추후 Spring Security 로그인 적용되면 SecurityContext에서 memberId를 가져오면 됨
        //Long TEMP_MEMBER_ID = 1L;

        //TEMP_MEMBER_ID->memberId
        ProductDetailResponse result = productQueryService.getProductDetail(memberId, productId);

        return ResponseEntity.ok(result);
    }

    //상품 등록
    @Operation(summary = "상품 등록", description = "상품 정보 + Supabase 이미지 URL")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductCreateResponse> createProduct
    (   @RequestBody ProductCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails)
        //@RequestPart : 아래처럼 작성하여, JSON + 파일을 각각 분리하여 받을수있게함
//        @RequestPart("data") ProductCreateRequest request,
//        // required = false : 스프링이 특정 요청 파트를 반드시 필요로 하지 않게 만드는 옵션,이미지가 없어도 에러를 내지 말고 images = null 로 (상품등록을)처리하라는 의미.
//        @RequestPart(value = "images",required = false) List<MultipartFile> images
    {
        if (userDetails == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        request.setSellerId(userDetails.getMemberId());//로그인 중 사용자id를 자동으로 sellerId로 설정

        Long newProductId = productCommandService.createProduct(request);
        ProductCreateResponse response =
            new ProductCreateResponse(newProductId, "상품이 정상적으로 등록되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        //HttpStatus.CREATED = 201
        //메서드 체이닝 - HTTP 201 Created 상태로 newProductId 를 body 에 담아 JSON 응답
//        return ResponseEntity.status(HttpStatus.CREATED).body(newProductId);
    }

    //비슷한 상품 조회
    @Operation(summary = "비슷한 상품 조회", description = "현 상품과 비슷한 상품 조회")
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductQueryResponse>> getSimilarProducts(@PathVariable Long productId) {

        return ResponseEntity.ok(productQueryService.findSimilarProducts(productId));
    }
    
    //상품 숨기기 //숨기기도 상품상태 update의 일종이므로 command로
    @Operation(summary = "상품 숨기기", description = "현재 노출중 상품 숨기기")
    @PatchMapping("/{productId}/hide")
    public ResponseEntity<Void> updateHidden(
        @PathVariable Long productId,
        @RequestBody HideRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        //로긴 상태확인
        if(userDetails==null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        //해당 상품 sellerId = 로그인Id 비교
        productCommandService.validateProductOwnership(productId,userDetails.getMemberId());

        productCommandService.updateHidden(productId, request.isHidden());
        return ResponseEntity.ok().build();
    }

    //상품수정(텍스트,이미지 통합)
    @Operation(summary = "상품 수정", description = "등록된 상품상세 수정")
    @PatchMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
        @PathVariable Long productId,
        @RequestBody ProductUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        if(userDetails==null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        productCommandService.validateProductOwnership(productId,userDetails.getMemberId());

        productCommandService.updateProduct(productId,request);
        return ResponseEntity.ok().build();
    }

    //상품 삭제-soft delete 방식
    @Operation(summary = "상품 삭제", description = "등록된 상품 삭제")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
        @AuthenticationPrincipal CustomUserDetails userDetails){
        if(userDetails==null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        productCommandService.validateProductOwnership(productId,userDetails.getMemberId());

        productCommandService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

}
