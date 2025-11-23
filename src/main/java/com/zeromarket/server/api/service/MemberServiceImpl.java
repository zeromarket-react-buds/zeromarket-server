package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.api.mapper.MemberMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.Role;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Long signup(MemberSignupRequest dto) {
        Member member = memberMapper.selectMemberByLoginId(dto.getLoginId()); // 유효성 검사? (loginId 중복 검사)

        if(member != null){
            throw new IllegalArgumentException("이미 존재하는 id 입니다.");
        }

//        dto -> entity
        member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setRole(Role.ROLE_USER.getDescription());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

//        db insert
        int affectedRows = memberMapper.insertMember(member);

//        결과: 성공
        if(affectedRows > 0){
            return member.getMemberId();
        }

//        결과: 실패
        throw new RuntimeException("회원 정보 삽입에 실패했습니다.");
    }


    @Override
    public TokenInfo login(MemberLoginRequest dto) {
        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(dto.getLoginId()))
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new SecurityException("비밀번호가 일치하지 않습니다.");
        }

        return new TokenInfo(
            jwtUtil.generateAccessToken(member.getLoginId(), member.getRole()),
            jwtUtil.generateRefreshToken(member.getLoginId())
        );
    }

    @Override
    public TokenInfo refresh(String refreshToken) {

//        1. null 확인
        if (refreshToken == null) {
            throw new AuthenticationCredentialsNotFoundException("JWT 토큰이 존재하지 않습니다.");
        }

//        2. access token 유효 확인
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.");
        }

        String loginId = jwtUtil.getLoginId(refreshToken);

        Member member = Optional.ofNullable(memberMapper.selectMemberByLoginId(loginId))
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(member.getLoginId(), member.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getLoginId());

        return new TokenInfo(newAccessToken, newRefreshToken);
    }
}
