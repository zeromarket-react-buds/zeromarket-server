package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;

public interface AuthService {
    TokenInfo login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);

    void logout(HttpServletResponse response);

    TokenInfo refresh(String refreshToken, HttpServletResponse response);

}
