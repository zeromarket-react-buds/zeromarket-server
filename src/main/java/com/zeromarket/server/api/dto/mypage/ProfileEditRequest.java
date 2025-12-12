package com.zeromarket.server.api.dto.mypage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProfileEditRequest {
    private String phone;
    private String email;
}
