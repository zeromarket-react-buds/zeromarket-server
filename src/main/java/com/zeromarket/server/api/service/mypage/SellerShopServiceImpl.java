package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.SalesProductCursorResponse;
import com.zeromarket.server.api.dto.mypage.SalesProductRequest;
import com.zeromarket.server.api.dto.product.ProductQueryResponse;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.ProductMapper;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SellerShopServiceImpl implements SellerShopService {

    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;

    // 셀러샵의 판매상품 조회
    @Override
    public SalesProductCursorResponse getProductsBySellerCursor(SalesProductRequest req) {
        //로긴유저id 전달시에만 회원여부체크 -
        if(req.getLoginMemberId()!=null){
            if(memberMapper.selectMemberById(req.getLoginMemberId()) == null) {
                throw new ApiException(ErrorCode.MEMBER_NOT_FOUND);
            }
        }

        List<ProductQueryResponse> items = productMapper.selectProductsBySellerCursor(req);

        boolean hasNext = items.size() == req.getSize();

        Long nextCursorProductId = null;
        LocalDateTime nextCursorCreatedAt = null;

        if (hasNext) {
            ProductQueryResponse last = items.get(items.size() - 1);
            nextCursorProductId = last.getProductId();
            nextCursorCreatedAt = last.getCreatedAt();
        }

        return new SalesProductCursorResponse(
            items,
            nextCursorProductId,
            nextCursorCreatedAt,
            hasNext
        );
    }
}
