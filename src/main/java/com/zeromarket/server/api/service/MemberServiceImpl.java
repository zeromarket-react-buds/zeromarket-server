package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.MemberLoginRequest;
import com.zeromarket.server.api.dto.MemberResponse;
import com.zeromarket.server.api.dto.MemberSignupRequest;
import com.zeromarket.server.api.dto.TokenInfo;
import com.zeromarket.server.api.mapper.MemberMapper;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.Role;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

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

        return null;
    }
}
