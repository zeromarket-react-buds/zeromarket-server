package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileEditResponse;
import com.zeromarket.server.api.dto.mypage.ProfileSettingRequest;
import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;

public interface ProfileService {
    ProfileSettingResponse selectProfileSetting(Long memberId);

    ProfileSettingResponse updateProfileSetting(Long memberId, ProfileSettingRequest request);

    boolean existsByNicknameExcludingMe(String nickname, Long memberId);

    ProfileEditResponse selectProfileEdit(Long memberId);
}
