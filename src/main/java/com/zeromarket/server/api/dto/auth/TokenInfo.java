package com.zeromarket.server.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {

    private String accessToken;
    private String refreshToken;

//    private String grantType;
//    private Long expiresIn;
}
