package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.mapper.product.ProductCustomTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//자주쓰는 문구 등록~
@Service
@RequiredArgsConstructor
public class ProductCustomTextCommandService {

    private final ProductCustomTextMapper mapper;
    //자주쓰는 문구 등록
    public void createProductCustomText(Long memberId, String text) {
        mapper.insertProductCustomText(memberId, "PRODUCT", text);
    }

    // 자주 쓰는 문구 삭제
    public void deleteProductCustomText(Long id) {
        mapper.softDeleteProductCustomText(id);
    }

    // 문구 수정
    public void updateProductCustomText(Long id, String text) {
        mapper.updateProductCustomText(id, text);
    }
}
