package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.ProductCreateRequest;
import com.zeromarket.server.api.mapper.ProductCommandMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService{

    private final ProductCommandMapper mapper;

    @Override
    public Long createProduct(ProductCreateRequest request, List<MultipartFile> images) {


        // 🔥 DB check constraint 맞추기
        if (request.getProductStatus() != null) {
            request.setProductStatus(request.getProductStatus().toUpperCase());
        }

        if (request.getSalesStatus() == null || request.getSalesStatus().isEmpty()) {
            request.setSalesStatus("FOR_SALE");  // 🔥 기본값
        }

        //상품정보 저장
        mapper.insertProduct(request);
        Long newProductId = request.getProductId();

        //이미지 url 디비에 저장(여기서 파일 URL은 프론트엔드에서 받은 Supabase URL)
        if(images!=null && !images.isEmpty()){
            for(MultipartFile file : images){
                String imageUrl = file.getOriginalFilename(); //url
                mapper.insertProductImage(newProductId, imageUrl); //디비에 url저장부분
            }
        }

        return newProductId;
    }


}
