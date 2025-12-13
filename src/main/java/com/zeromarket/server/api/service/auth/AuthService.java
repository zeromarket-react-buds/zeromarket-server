package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest);

    TokenInfo refresh(String refreshToken);

    MemberResponse getMyInfo(String loginId);

    Boolean checkDuplicateId(String loginId);

    MemberProfileDto getMemberProfile(Long memberId, Long authMemberId);

    String loginWithKakao(String code, HttpServletResponse response);
}
