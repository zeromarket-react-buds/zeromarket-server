package com.zeromarket.server.api.service.mypage;

import com.zeromarket.server.api.dto.mypage.ProfileSettingRequest;
import com.zeromarket.server.api.dto.mypage.ProfileSettingResponse;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.api.mapper.mypage.ProfileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper mapper;
    private final MemberMapper memberMapper;

    // 프로필 조회
    @Override
    public ProfileSettingResponse selectProfileSetting(Long memberId) {
        return mapper.selectProfileSetting(memberId);
    }

    // 프로필 변경
    @Override
    public ProfileSettingResponse updateProfileSetting(Long memberId, ProfileSettingRequest request) {

        mapper.updateProfileSetting(memberId, request);

        // 수정 후 최신 값 다시 조회해서 반환
        return mapper.selectProfileSetting(memberId);
    }

    @Override
    public boolean existsByNicknameExcludingMe(String nickname, Long memberId) {
        return memberMapper.existsByNicknameExcludingMe(nickname, memberId);
    }
}
