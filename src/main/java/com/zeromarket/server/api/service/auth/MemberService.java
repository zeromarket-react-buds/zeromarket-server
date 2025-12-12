package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.MemberLoginRequest;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.MemberResponse;
import com.zeromarket.server.api.dto.auth.MemberSignupRequest;
import com.zeromarket.server.api.dto.auth.TokenInfo;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;

public interface MemberService {

    Long signup(MemberSignupRequest memberSignupRequest);

    TokenInfo login(MemberLoginRequest memberLoginRequest);

    TokenInfo refresh(String refreshToken);

    MemberResponse getMyInfo(String loginId);

    Boolean checkDuplicateId(String loginId);

    MemberProfileDto getMemberProfile(Long memberId, Long authMemberId);

    MemberEditResponse getMemberEdit(Long memberId);

    MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request);
}
