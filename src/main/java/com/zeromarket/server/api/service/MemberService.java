package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.common.entity.Member;

public interface MemberService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest);
}
