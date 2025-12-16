package com.zeromarket.server.api.mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AreaQueryMapper {

    Long getLegalDongIdByLegalCode(@Param("code") String legalDongCode);
}
