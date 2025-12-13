package com.zeromarket.server.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginRequest {

    private String code;
}
