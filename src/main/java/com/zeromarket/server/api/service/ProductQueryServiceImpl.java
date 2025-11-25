package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.LoadMoreResponse;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import com.zeromarket.server.api.mapper.ProductQueryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

        List<ProductQueryResponse> fetched = mapper.selectProductsCursor(queryReq);

        boolean hasNext = fetched.size() > size;
        if (hasNext) fetched = fetched.subList(0, size);

        Long nextCursor = fetched.isEmpty()
                ? null
                : fetched.get(fetched.size() - 1).getProductId();

        return LoadMoreResponse.of(fetched, nextCursor, hasNext);
    }
}
