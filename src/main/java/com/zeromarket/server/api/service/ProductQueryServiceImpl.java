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
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductQueryMapper mapper;

    @Override
    public LoadMoreResponse<ProductQueryResponse> selectProductList(ProductQueryRequest req) {
        int size = (req.getSize() == null || req.getSize() < 1) ? 10 : req.getSize();

        // cursor를 offset으로 사용
        Long offset = req.getOffset();
        int safeOffset = (offset == null || offset < 0) ? 0 : offset.intValue();

        ProductQueryRequest queryReq = new ProductQueryRequest();
        queryReq.setSize(size + 1);                 // hasNext 판단용(다음 페이지가 있는지 체크용 + 1)
        queryReq.setOffset((long) safeOffset);      // mapper에서 OFFSET 으로 사용
        queryReq.setKeyword(req.getKeyword());
        queryReq.setSort(req.getSort());
        queryReq.setCategory(req.getCategory());
        queryReq.setMinPrice(req.getMinPrice());
        queryReq.setMaxPrice(req.getMaxPrice());
        queryReq.setArea(req.getArea());

        List<ProductQueryResponse> fetched = mapper.selectProductsOffset(queryReq);

        boolean hasNext = fetched.size() > size;
        if (hasNext) {
            fetched = fetched.subList(0, size);
        }

        // 다음 offset = 현재 offset + 이번에 실제로 보낸 개수
        Long nextOffset = hasNext
            ? (long) (safeOffset + size)
            : null;

        return LoadMoreResponse.of(fetched, nextOffset, hasNext);
    }

    @Override
    public ProductDetailResponse selectProductDetail(Long productId) {
//        mapper.updateViewCount(productId); 조회수증가 중복으로삭제
        ProductDetailResponse detail = mapper.selectProductDetail(productId);
        if (detail == null)
            return null;

        Integer mainIndex = null;
        for (int i = 0; i < detail.getImages().size(); i++) {
            if (detail.getImages().get(i).isMain()) {
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

    @Override
    public List<ProductQueryResponse> findSimilarProducts(Long productId) {
        return mapper.selectSimilarProducts(productId);
    }


}