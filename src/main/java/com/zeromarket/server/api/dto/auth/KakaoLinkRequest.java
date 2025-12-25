package com.zeromarket.server.api.dto.auth;

import lombok.Data;

@Data
public class KakaoLinkRequest {
    private String code;
    private String redirectUri;
}
