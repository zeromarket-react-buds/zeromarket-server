package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.api.mapper.auth.MemberMapper;
import com.zeromarket.server.common.entity.Member;
import com.zeromarket.server.common.enums.Role;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

//    TODO: 프로필 이미지는 저장 안되는 것 같은데?

    @Transactional
    public Member findOrCreateKakaoUser(KakaoUserInfo kakaoUserInfo) {

        // 1. social_id 생성
        String socialId = "kakao_" + kakaoUserInfo.getId();

        // 2. 이미 가입된 회원 조회
        Member member = memberMapper.findBySocialId(socialId);
        if (member != null) {
            return member;
        }

        // 3. 안전하게 값 꺼내기 (null 방어)
        KakaoUserInfo.KakaoAccount account = kakaoUserInfo.getKakao_account();
        KakaoUserInfo.KakaoAccount.Profile profile =
            account != null ? account.getProfile() : null;

        System.out.println(profile.getNickname()); // 황희원
        System.out.println(profile.getProfileImageUrl()); // null

        String nicknameBase = profile != null ? profile.getNickname() : "카카오사용자";
        String profileImageUrl =
            profile != null ? profile.getProfileImageUrl() : null;

        int maxRetry = 5;

//        TODO: '카카오사용자' -> nickname unique 제약 조건 위반
        for(int i = 0; i < maxRetry; i++) {
            String suffix = UUID.randomUUID().toString().substring(0, 4);
             String nickname = nicknameBase + "_" + suffix;

            // 4. 신규 회원 생성
            Member newMember = new Member();
            newMember.setSocialId(socialId);
            newMember.setNickname(nickname);
            newMember.setProfileImage(profileImageUrl);
            newMember.setRole(Role.ROLE_USER.getDescription());
            // 더미 데이터
            newMember.setLoginId(socialId);
            newMember.setPassword("{noop}SOCIAL_LOGIN");

            try {
                memberMapper.insertSocialMember(newMember);
                return newMember;
            } catch(DuplicateKeyException e) {
                // nickname UNIQUE 충돌 → 다시 시도
                if (i == maxRetry - 1) {
                    throw new IllegalStateException("닉네임 생성에 실패했습니다. 잠시 후 다시 시도해주세요.", e);
                }
            }
        }

        // 논리적으로 도달하지 않음
        throw new IllegalStateException("닉네임 생성 실패");
    }
}
