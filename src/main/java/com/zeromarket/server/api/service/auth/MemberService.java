package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;

public interface MemberService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest);

    TokenInfo refresh(String refreshToken);

    MemberResponse getMyInfo(String loginId);

    Boolean checkDuplicateId(String loginId);
}
