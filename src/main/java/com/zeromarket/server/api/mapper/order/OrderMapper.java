package com.zeromarket.server.api.mapper.order;

import com.zeromarket.server.common.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    void insertOrder(Order order);

    void updateOrderStatus(
        @Param("orderId") Long orderId,
        @Param("status") String status
    );
}
