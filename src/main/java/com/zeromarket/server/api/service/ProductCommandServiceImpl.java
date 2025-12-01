package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.ProductCreateRequest;
import com.zeromarket.server.api.dto.ProductCreateRequest.ProductImageDto;
import com.zeromarket.server.api.mapper.ProductCommandMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductCommandMapper mapper;

    @Override
    @Transactional
    public Long createProduct(ProductCreateRequest request) {

        if (request.getProductStatus() != null) {
            request.setProductStatus(request.getProductStatus().toUpperCase());
        }

        if (request.getSalesStatus() == null || request.getSalesStatus().isEmpty()) {
            request.setSalesStatus("FOR_SALE"); //기본값
        }

        //상품정보 저장
        mapper.insertProduct(request);
        Long newProductId = request.getProductId();

        List<ProductCreateRequest.ProductImageDto> images = request.getImages();

        //이미지 없는 상품등록 - 여기서 종료
        if (images == null || images.isEmpty()) {
            return newProductId;
        }
        //이미지 있는 상품등록 - 디비에 insert
        if (request.getImages() != null) {
            for (ProductCreateRequest.ProductImageDto img : images) {
                mapper.insertProductImage(
                    newProductId,
                    img.getImageUrl(),
                    img.getSortOrder(),
                    img.getIsMain()
                );
            }

        }
        return newProductId; //여기까지 예외없이끝나면 트랜잭션이 커밋됨..
    }

    @Override
    public void updateHidden(Long productId, boolean hidden) {
        mapper.updateHidden(productId,hidden);

    }

    @Override
    public void deleteProduct(Long productId) {
        mapper.softDeleteProduct(productId);
    }


}
