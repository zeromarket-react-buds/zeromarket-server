package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;

public interface AuthService {
    TokenInfo login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);

    FindAccountResponse findLoginId(FindAccountRequest findAccountRequest);

    void logout(HttpServletResponse response);

    TokenInfo refresh(String refreshToken, HttpServletResponse response);

}
