package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.mapper.product.CustomTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//자주쓰는 문구 등록~
@Service
@RequiredArgsConstructor
public class CustomTextCommandService {

    private final CustomTextMapper mapper;

    // 자주 쓰는 문구 등록 (PRODUCT / CHAT 공용)
    public void createCustomText(
            Long memberId,
            String contentType,
            String text
    ) {
        mapper.insertProductCustomText(memberId, contentType, text);
    }

    // 자주 쓰는 문구 삭제
    public void deleteCustomText(Long id, Long memberId) {
        int deleted = mapper.softDeleteProductCustomText(id, memberId);
        if (deleted == 0) {
            throw new IllegalStateException("삭제 실패: 권한 없음 또는 데이터 없음");
        }
    }

    // 자주 쓰는 문구 수정
    public void updateCustomText(Long id, Long memberId, String text) {
        int updated = mapper.updateProductCustomText(id, memberId, text);
        if (updated == 0) {
            throw new IllegalStateException("수정 실패: 권한 없음 또는 데이터 없음");
        }
    }
}
