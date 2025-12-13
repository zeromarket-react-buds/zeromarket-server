package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.common.entity.Member;

public interface MemberService {
    Member findOrCreateKakaoUser(KakaoUserInfo userInfo);
}
