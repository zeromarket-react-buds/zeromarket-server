package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import com.zeromarket.server.api.mapper.mypage.ProfileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper mapper;

    @Override
    public ProfileSettingResponse selectProfileSetting(Long memberId) {
        return mapper.selectProfileSetting(memberId);
    }
}
