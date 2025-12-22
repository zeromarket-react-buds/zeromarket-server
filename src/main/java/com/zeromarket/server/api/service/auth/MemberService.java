package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.auth.WithdrawRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.common.entity.Member;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    MemberProfileDto getMemberProfile(Long memberId, Long authMemberId);

    MemberEditResponse getMemberEdit(Long memberId);

    MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request);

    Member findOrCreateKakaoUser(KakaoUserInfo userInfo);

    void withdraw(Long memberId, WithdrawRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);
}
