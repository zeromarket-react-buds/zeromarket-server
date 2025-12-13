package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;

public interface OAuthLoginService {
    String getAccessToken(String code);
    KakaoUserInfo getUserInfo(String accessToken);
}
