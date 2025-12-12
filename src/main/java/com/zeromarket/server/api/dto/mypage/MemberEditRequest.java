package com.zeromarket.server.api.dto.mypage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberEditRequest {
    private String phone;
    private String email;
}
