package com.zeromarket.server.api.mapper.order;

import com.zeromarket.server.api.dto.order.MemberAddressDto;
import com.zeromarket.server.api.dto.order.MemberAddressRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberAddressMapper {
    void insert(Long memberId, MemberAddressRequest req, boolean isDefault);
    void clearDefault(Long memberId);
    int countActiveByMember(Long memberId);
    List<MemberAddressDto> findAll(Long memberId);
    MemberAddressDto findById(
        @Param("addressId") Long addressId,
        @Param("memberId") Long memberId
    );
}
