package com.zeromarket.server.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private String refreshToken;

    public TokenInfo(String accessToken) {
        this.accessToken = accessToken;
    }
}
