package com.zeromarket.server.api.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileSettingRequest {
    private String profileImage;
    private String nickname;
    private String introduction;
}
