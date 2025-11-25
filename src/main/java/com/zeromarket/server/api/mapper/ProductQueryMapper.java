package com.zeromarket.server.api.mapper;

import com.zeromarket.server.api.dto.ProductDetailImageInfo;
import com.zeromarket.server.api.dto.ProductDetailResponse;
import com.zeromarket.server.api.dto.ProductDetailSellerInfo;
import com.zeromarket.server.api.dto.ProductQueryRequest;
import com.zeromarket.server.api.dto.ProductQueryResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductQueryMapper {

    List<ProductQueryResponse> selectProductsCursor(ProductQueryRequest queryReq);

    ProductDetailResponse selectProductDetail(Long productId);

    List<ProductDetailImageInfo> selectProductImages(Long productId);

    ProductDetailSellerInfo selectProductSeller(Long memberId);


}
