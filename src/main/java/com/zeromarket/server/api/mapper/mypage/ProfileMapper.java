package com.zeromarket.server.api.mapper.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileEditResponse;
import com.zeromarket.server.api.dto.mypage.ProfileSettingRequest;
import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfileMapper {
    ProfileSettingResponse selectProfileSetting(@Param("memberId") Long memberId);

    void updateProfileSetting(@Param("memberId") Long memberId,
                              @Param("request") ProfileSettingRequest request);

    ProfileEditResponse selectProfileEdit(Long memberId);
}
