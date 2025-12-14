package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.common.entity.Member;

public interface MemberService {
    MemberProfileDto getMemberProfile(Long memberId, Long authMemberId);

    MemberEditResponse getMemberEdit(Long memberId);

    MemberEditResponse updateMemberEdit(Long memberId, MemberEditRequest request);

    Member findOrCreateKakaoUser(KakaoUserInfo userInfo);
}
