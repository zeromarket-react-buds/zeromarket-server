package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.product.ProductCreateRequest;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductUpdateRequest;
import com.zeromarket.server.api.dto.product.ProductVisionResponse;
import com.zeromarket.server.api.mapper.product.ProductCommandMapper;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.ProductStatus;
import com.zeromarket.server.common.enums.SalesStatus;
import com.zeromarket.server.common.exception.ApiException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductCommandMapper mapper;
    private final VisionService visionService;

    // 상품 등록 전 vision. VisionService로 흐름 연결
    @Override
    public ProductVisionResponse productVisionAnalyze(byte[] bytes, String contentType) {
        return visionService.analyze(bytes, contentType);
    }

    @Override
    @Transactional
    public Long createProduct(ProductCreateRequest request) {

        //로그인한 사람의 id를 sellerId 로 설정
        if(request.getSellerId() == null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getProductTitle() == null || request.getProductTitle().isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        if (request.getSellPrice() == null || request.getSellPrice() < 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

         if (request.getCategoryDepth1() == null ||
             request.getCategoryDepth2() == null ||
             request.getCategoryDepth3() == null ) {
             throw new ApiException(ErrorCode.INVALID_REQUEST);
         }

        if (request.getProductStatus() != null) {
            String status = request.getProductStatus().toUpperCase();
            try{
                ProductStatus.valueOf(status);//존재하는enum인지 검증
            }catch (Exception e){
                throw new ApiException(ErrorCode.INVALID_REQUEST);
            }
            request.setProductStatus(status);
        }

        if (request.getSalesStatus() == null || request.getSalesStatus().isEmpty()) {
            request.setSalesStatus("FOR_SALE"); //기본값
        }else{
            String sales = request.getSalesStatus().toUpperCase();
            try{
                SalesStatus.valueOf(sales);
            }catch (Exception e){
                throw new ApiException(ErrorCode.INVALID_REQUEST);
            }
            request.setSalesStatus(sales);
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
        if (images != null) {
//        if (request.getImages() != null) {
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

    //상품수정(텍스트+이미지)
    @Override
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequest request) {

        if (request.getProductTitle() != null && request.getProductTitle().isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        if (request.getSellPrice() != null && request.getSellPrice() < 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        if (request.getProductStatus() != null) {
            String status = request.getProductStatus().toUpperCase();
            try {
                ProductStatus.valueOf(status);
            } catch (Exception e) {
                throw new ApiException(ErrorCode.INVALID_REQUEST);
            }
            request.setProductStatus(status);
        }

        //텍스트,기본정보수정
        mapper.updateProduct(productId,request);

        //이미지 수정-이미지 리스트null이면 변경X
        if(request.getImages()==null){
            return;
        }

        //기존이미지 전부 삭제
        mapper.deleteImagesByProductId(productId);

        //새 이미지 순서대로 다시 삽입
        for(ProductUpdateRequest.ImageDto img : request.getImages()){
            mapper.insertProductImage(
                productId,
                img.getImageUrl(),
                img.getSortOrder(),
                img.getIsMain()//프론트에서 정한 대표이미지
            );

        }

    }
    //상품 소유자 확인 메서드
    @Override
    public void validateProductOwnership(Long productId,Long loggedInUserId){
        //상품상세정보 조회
        Long sellerId = mapper.getProductSellerId(productId);
        //상품소유자, 로그인 사용자id 비교
        if(!sellerId.equals(loggedInUserId)){
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    public void createProductLocation(Long newProductId, ProductCreateRequest request) {
        mapper.insertProductLocation(newProductId,request);

    }


}
