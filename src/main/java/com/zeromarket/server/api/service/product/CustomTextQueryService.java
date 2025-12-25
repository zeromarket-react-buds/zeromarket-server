package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.ProductCustomTextResponse;
import com.zeromarket.server.api.mapper.product.ProductCustomTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//자주쓰는문구 목록 불러오기
@Service
@RequiredArgsConstructor
public class CustomTextQueryService {

    private final ProductCustomTextMapper mapper;

    // 자주 쓰는 문구 목록 조회 (PRODUCT / CHAT 공용)
    public List<ProductCustomTextResponse> getCustomTexts(
            Long memberId,
            String contentType
    ) {
        return mapper.findProductCustomTexts(memberId, contentType);
    }
}
