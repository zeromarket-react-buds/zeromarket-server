package com.zeromarket.server.api.mapper.product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AreaQueryMapper {

    Long getEupmyeondongIdByLegalCode(@Param("code") String eightDigitCode);
}
