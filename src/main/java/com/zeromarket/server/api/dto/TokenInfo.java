package com.zeromarket.server.api.dto;

import lombok.Data;

@Data
public class TokenInfo {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;
}
