package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.SalesProductCursorResponse;
import com.zeromarket.server.api.dto.mypage.SalesProductRequest;

public interface SellerShopService {

    SalesProductCursorResponse getProductsBySellerCursor(SalesProductRequest req);
}
