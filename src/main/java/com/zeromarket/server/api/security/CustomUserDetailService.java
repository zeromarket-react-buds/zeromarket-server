package com.zeromarket.server.api.security;

import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//JWT 환경에서도 UserDetailsService는
// “Security 생태계가 principal 타입을 알 수 있게” 해주기 때문에 필요하다.

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(loginId))
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return new CustomUserDetails(
            member.getMemberId(),
            member.getLoginId(),
            member.getRole()
        );
    }
}
