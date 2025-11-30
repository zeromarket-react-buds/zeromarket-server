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
//        //첨부이미지 url 리스트
//        List<String> imageUrls = request.getImageUrls();

        //이미지 없는 상품등록 - 여기서 종료
//        if(imageUrls == null||imageUrls.isEmpty()){
//            return newProductId;
//        }
//
//        //이미지 있는 상품등록 - product_image 테이블 insert
//        int mainIdx = (request.getMainImageIndex() !=null)
//            ? request.getMainImageIndex()
//            :0; //null이면 첫번째를 대표로
//        int sortOrder = 0;
//        for(String imageUrl : imageUrls){ // imageUrls에 든 값을 꺼내 변수imageUrl에 하나씩 담기 반복
//            boolean isMain = (sortOrder ==mainIdx); //첫번째사진을 대표이미지로 ?
//            mapper.insertProductImage(newProductId,imageUrl,sortOrder,isMain);
//            sortOrder++;
//        }

        //이미지 url 디비에 저장(여기서 파일 URL은 프론트엔드에서 받은 Supabase URL)
//        if(images!=null && !images.isEmpty()){
//            for(MultipartFile file : images){
//                String imageUrl = file.getOriginalFilename(); //url
//                mapper.insertProductImage(newProductId, imageUrl); //디비에 url저장부분
//            }
//        }
//
//        return newProductId;
    }


}
