package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductDetailImageInfo;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductDetailSellerInfo;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.mapper.ProductQueryMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService{

    private final ProductQueryMapper mapper;

    @Override
    public LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest req) {
        int size = (req.getSize() == null || req.getSize() < 1) ? 10 : req.getSize();
        Long cursor = req.getCursor();

        ProductQueryRequest queryReq = new ProductQueryRequest();
        queryReq.setSize(size + 1);
        queryReq.setCursor(cursor);
        queryReq.setKeyword(req.getKeyword());
        queryReq.setSort(req.getSort());

        List<ProductQueryResponse> fetched = mapper.selectProductsCursor(queryReq);

        boolean hasNext = fetched.size() > size;
        if (hasNext) fetched = fetched.subList(0, size);

        Long nextCursor = fetched.isEmpty()
                ? null
                : fetched.get(fetched.size() - 1).getProductId();

        return LoadMoreResponse.of(fetched, nextCursor, hasNext);
    }

    @Override
    public ProductDetailResponse selectProductDetail(Long productId) {
        ProductDetailResponse detail = mapper.selectProductDetail(productId);
        if(detail == null) return null;

        //판매자 정보
        ProductDetailSellerInfo seller =
            mapper.selectProductSeller(detail.getSellerId());
        detail.setSeller(seller);

        List<ProductDetailImageInfo> images = mapper.selectProductImages(productId);
        detail.setImages(images);

        Integer mainIndex = null;
        for (int i = 0; i < images.size(); i++){
            if(images.get(i).isMain()){
                mainIndex = i;
                break;
            }
        }
        detail.setMainImageIndex(mainIndex);

        return detail;

    }


}
