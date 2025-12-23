package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import com.zeromarket.server.api.mapper.product.ProductCustomTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//자주쓰는문구 목록 불러오기
@Service
@RequiredArgsConstructor
public class ProductCustomTextQueryService {

    private final ProductCustomTextMapper mapper;

    public List<ProductCustomTextResponse> getProductCustomTexts(
            Long memberId
    ) {
        return mapper.findProductCustomTexts(memberId, "PRODUCT");
    }
}
