package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductDetailImageInfo;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductDetailSellerInfo;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.dto.WishCountResponse;
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
//        mapper.updateViewCount(productId); 조회수증가 중복으로삭제
        ProductDetailResponse detail = mapper.selectProductDetail(productId);
        if(detail == null) return null;

        detail.setProductStatusKr(convertProductStatusToKr(detail.getProductStatus()));
//        detail.setSalesStatusKr(convertSalesStatusToKr(detail.getSalesStatus()));

        Integer mainIndex = null;
        for (int i = 0; i < detail.getImages().size(); i++){
            if(detail.getImages().get(i).isMain()){
                mainIndex = i;
                break;
            }
        }
        detail.setMainImageIndex(mainIndex);

        return detail;

    }

    @Override
    public void increaseViewCount(Long productId) {
        mapper.updateViewCount(productId);
    }

    @Override
    public WishCountResponse getWishCount(Long productId) {
        int count = mapper.countWishByProductId(productId);
        WishCountResponse dto = new WishCountResponse();
        dto.setProductId(productId);
        dto.setWishCount(count);
        return dto;
    }


    private String convertProductStatusToKr(String status) {
        if (status == null) return null;

        return switch (status) {
            case "UNOPENED" -> "미개봉";
            case "OPENED_UNUSED" -> "개봉·미사용";
            case "USED" -> "중고";
            default -> status;
        };
    }

//    private String convertSalesStatusToKr(String status) {
//        if (status == null) return null;
//
//        return switch (status) {
//            case "FOR_SALE" -> "판매중";
//            case "RESERVED" -> "예약중";
//            case "SOLD_OUT" -> "판매완료";
//            default -> status;
//        };
//    }
}
