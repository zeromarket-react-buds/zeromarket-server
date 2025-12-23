package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;

public interface AuthService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest, HttpServletResponse response);

    TokenInfo refresh(String refreshToken, HttpServletResponse response);

    MemberResponse getMyInfo(String loginId);

    Boolean checkDuplicateId(String loginId);

    MemberProfileDto getMemberProfile(Long memberId, Long authMemberId);
}
