package com.zeromarket.server.api.service.product;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.product.ProductDetailResponse;
import com.zeromarket.server.api.dto.product.ProductQueryRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        queryReq.setCategoryId(req.getCategoryId());
        queryReq.setMinPrice(req.getMinPrice());
        queryReq.setMaxPrice(req.getMaxPrice());
        queryReq.setArea(req.getArea());
        queryReq.setTrade(req.getTrade());
        queryReq.setMemberId(req.getMemberId());//찜 여부 계산위한(새로고침해도 빨강 고정)

        queryReq.setSwLat(req.getSwLat());
        queryReq.setSwLng(req.getSwLng());
        queryReq.setNeLat(req.getNeLat());
        queryReq.setNeLng(req.getNeLng());
        queryReq.setLatitude(req.getLatitude());
        queryReq.setLongitude(req.getLongitude());

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
    @Transactional
    public ProductDetailResponse getProductDetail(Long memberId,Long productId) {
        //조회수 증가
        mapper.updateViewCount(productId);
        // 🔥 변경된 부분: Map으로 두 개의 파라미터 전달
        // 🔥 memberId + productId 두 개를 map으로 넘겨서 XML에서 #{memberId} 사용 가능하게 함
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("memberId", memberId); //이게 null이면 XML에서 null 바인딩

        // ⭐ Map을 넘겨야 XML에서 #{memberId} 사용 가능
        ProductDetailResponse detail = mapper.selectProductDetail(params);
        // ProductDetailResponse detail = mapper.selectProductDetail(productId);

        if(detail==null){
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if(detail.isDeleted()){
            throw new ApiException(ErrorCode.DELETED_PRODUCT);
        }
        if(detail.isHidden() && !detail.getSellerId().equals(memberId)){
            throw new ApiException(ErrorCode.HIDDEN_PRODUCT);
        }
        return detail;
    }

    @Override
    public List<ProductQueryResponse> findSimilarProducts(Long productId) {
        return mapper.selectSimilarProducts(productId);
    }

    //여기서부터 타고가면 될거 같은...?? 느낌..??


}