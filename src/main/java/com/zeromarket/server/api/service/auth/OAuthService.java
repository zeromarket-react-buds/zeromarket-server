package com.zeromarket.server.api.service.auth;

import jakarta.servlet.http.HttpServletResponse;

public interface OAuthService {
    String loginWithKakao(String code, HttpServletResponse response);
}
