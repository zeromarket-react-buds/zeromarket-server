package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;

public interface MemberService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest);

    TokenInfo refresh(String refreshToken);

    MemberResponse getMyInfo(String loginId);

    Boolean checkDuplicateId(String loginId);
}
