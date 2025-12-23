package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.mapper.product.ProductCustomTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//자주쓰는 문구 등록
@Service
@RequiredArgsConstructor
public class ProductCustomTextCommandService {

    private final ProductCustomTextMapper mapper;

    public void createProductCustomText(Long memberId, String text) {
        mapper.insertProductCustomText(memberId, "PRODUCT", text);
    }
}
