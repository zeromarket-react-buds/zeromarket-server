package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.api.mapper.MemberMapper;
import com.zeromarket.server.api.security.JwtUtil;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.Role;
import com.zeromarket.server.common.exception.ApiException;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

//    email, loginId, nickname, phone (unique)

    @Override
    public Long signup(MemberSignupRequest dto) {
//        email 필드가 빈 문자열("")인 경우, Null 값으로 변경(DB 저장을 위해)
        String email = dto.getEmail();
        if(email == null || email.isBlank()) {
            dto.setEmail(null);
        }

//        unique 필드 검증
        if(memberMapper.existsByLoginId(dto.getLoginId())) {
            throw new ApiException(ErrorCode.LOGINID_ALREADY_EXIST);
        }

        if(memberMapper.existsByNickname(dto.getNickname())) {
            throw new ApiException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }

        if(memberMapper.existsByPhone(dto.getPhone())) {
            throw new ApiException(ErrorCode.PHONE_ALREADY_EXIST);
        }

        if(dto.getEmail() != null && memberMapper.existsByEmail(dto.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

//        Member member = memberMapper.selectMemberByLoginId(dto.getLoginId()); // 유효성 검사? (loginId 중복 검사)
//        if(member != null){
////            throw new IllegalArgumentException("이미 존재하는 id 입니다.");
//            throw new ApiException(ErrorCode.LOGINID_ALREADY_EXIST);
//        }

//        dto -> entity
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setRole(Role.ROLE_USER.getDescription());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
////        "" --> Null
//        String email = member.getEmail();
//        if(email == null || email.isBlank()) {
//            member.setEmail(null);
//        }

        try {
            memberMapper.insertMember(member);
            return member.getMemberId();
        } catch (DuplicateKeyException e) {
            throw new ApiException(ErrorCode.DUPLICATE_RESOURCE);
        }
////        db insert
//        int affectedRows = memberMapper.insertMember(member);
//
////        결과: 성공
//        if(affectedRows > 0){
//            return member.getMemberId();
//        }
//
////        결과: 실패
//        throw new ApiException(ErrorCode.DB_INSERT_FAILED);
////        throw new RuntimeException("회원 정보 삽입에 실패했습니다.");
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

    @Override
    public MemberResponse getMyInfo() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Member member = memberMapper.selectMemberByLoginId(loginId);

        MemberResponse response = new MemberResponse();
        BeanUtils.copyProperties(member, response);

        return response;
    }

    @Override
    public Boolean checkDuplicateId(String loginId) {
        boolean existsByLoginId = memberMapper.existsByLoginId(loginId);

        return  existsByLoginId;
    }

//    @Override
//    public Member getMyInfo(Authentication authentication) {
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new UnauthorizedException("로그인이 필요합니다.");
//        }
//
//        String loginId = authentication.getName();
//
//        return Optional.ofNullable(memberMapper.selectMemberByLoginId(loginId))
//            .orElseThrow(() ->
//                new MemberNotFoundException("해당 회원을 찾을 수 없습니다.")
//            );
//    }

}
