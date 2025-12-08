package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;

public interface ProfileService {
    ProfileSettingResponse selectProfileSetting(Long memberId);
}
