package com.zeromarket.server.api.mapper.trade;

import com.zeromarket.server.api.dto.order.*;
import com.zeromarket.server.api.dto.mypage.TradeReviewInfoDto;

import com.zeromarket.server.api.dto.trade.*;
import com.zeromarket.server.common.entity.Trade;
import java.time.LocalDateTime;
import java.util.Map;

import com.zeromarket.server.common.enums.OrderStatus;
import com.zeromarket.server.common.enums.SalesStatus;
import com.zeromarket.server.common.enums.TradeStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TradeHistoryMapper {
    List<TradeHistoryResponse> selectTradeList(TradeHistoryRequest req);

    TradeProductResponse selectTradeProduct(TradeProductRequest req);

    TradeReviewInfoDto selectTradeInfoForReview(
        @Param("tradeId") Long tradeId,
        @Param("loginMemberId") Long loginMemberId
    );

    TradeStatusUpdateRow selectById(Long tradeId);
    
    Map<String, Object> selectSellerBuyerStatusByTradeId(Long tradeId);

    void updateTradeStatus(Long tradeId, TradeStatus target, LocalDateTime completedAt, LocalDateTime canceledAt, String canceledBy, LocalDateTime updatedAt);

    void updateOrderStatus(@Param("tradeId") Long tradeId,
                           @Param("orderStatus") OrderStatus orderStatus,
                           @Param("updatedAt") LocalDateTime updatedAt);

    void updateProductSalesStatus(@Param("productId") Long productId,
                                  @Param("salesStatus") SalesStatus salesStatus);

    int createTrade(TradeRequest tradeRequest);

    Trade existValidTradeByProductIdSellerId(@Param("productId") Long productId, @Param("sellerId") Long sellerId, @Param("buyerId") Long buyerId);

    void updateSoftDelete(Long tradeId, boolean sellerDeleted, boolean buyerDeleted, LocalDateTime updatedAt);

    List<Trade> existValidProcessingTradeByProductIdSellerId(@Param("productId") Long productId, @Param("sellerId") Long sellerId, @Param("buyerId") Long buyerId);
}
