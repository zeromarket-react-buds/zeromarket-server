package com.zeromarket.server.api.controller.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextCreateRequest;
import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import com.zeromarket.server.api.dto.product.ProductCustomTextUpdateRequest;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.product.CustomTextCommandService;
import com.zeromarket.server.api.service.product.CustomTextQueryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/custom-texts")
public class CustomTextController {

    private final CustomTextQueryService queryService;//목록불러오기
    private final CustomTextCommandService commandService;//문구 등록, 삭제

    //목록 불러오기
    @GetMapping
    public List<ProductCustomTextResponse> getCustomTexts(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String contentType
    ) {
        return queryService.getCustomTexts(
                user.getMemberId(),
                contentType
        );
    }


    //문구등록
    @PostMapping
    public void createCustomText(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateCustomTextRequest request
    ) {
        commandService.createCustomText(
                user.getMemberId(),
                request.getContentType(), // CHAT / PRODUCT
                request.getText()
        );
    }

    //삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomText(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        commandService.deleteCustomText(id, user.getMemberId());
    }

    //수정
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCustomText(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id,
            @RequestBody UpdateCustomTextRequest request
    ) {
        commandService.updateCustomText(
                id,
                user.getMemberId(),
                request.getText()
        );
    }
    @Getter
    static class CreateCustomTextRequest {
        private String contentType; // CHAT / PRODUCT
        private String text;
    }

    @Getter
    static class UpdateCustomTextRequest {
        private String text;
    }
}
