package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfileMapper {
    ProfileSettingResponse selectProfileSetting(@Param("memberId") Long memberId);
}
