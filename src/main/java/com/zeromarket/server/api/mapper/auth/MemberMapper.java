package com.zeromarket.server.api.mapper.auth;

import com.zeromarket.server.api.dto.auth.MemberProfileDto;
import com.zeromarket.server.api.dto.mypage.MemberEditRequest;
import com.zeromarket.server.api.dto.mypage.MemberEditResponse;
import com.zeromarket.server.common.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    // 회원 조회
    Member selectMemberById(Long memberId);
    Member selectMemberByIdWithWithdrawn(Long memberId);
    Member selectMemberByLoginId(String loginId);
    Member selectMemberByLoginIdWithWithdrawn(String loginId);
    MemberProfileDto selectMemberProfile(Long memberId);
    MemberEditResponse getMemberEdit(Long memberId);

    // 중복 체크
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);
    boolean existsByNicknameExcludingMe(@Param("nickname") String nickname,
        @Param("memberId") Long memberId);

    // 회원 생성
    int insertMember(Member member);

    // 회원정보 설정 페이지에서 해당 회원 정보 수정
    void updateMemberEdit(
        @Param("memberId") Long memberId,
        @Param("request") MemberEditRequest request
    );

    // 회원 탈퇴
    int withdrawMember(@Param("memberId") Long memberId,
        @Param("withdrawalReasonId") Integer withdrawalReasonId,
        @Param("withdrawalReasonDetail") String withdrawalReasonDetail);

    // OAuth
    Member findBySocialId(@Param("socialId") String socialId);
    Member findBySocialIdWithWithdrawn(@Param("socialId") String socialId);
    void insertSocialMember(Member member);
    void reactivateMember(@Param("memberId") Long memberId,
                          @Param("loginId") String loginId,
                          @Param("nickname") String nickname,
                          @Param("profileImage") String profileImage,
                          @Param("socialId") String socialId);
    void updateSocialId(@Param("memberId") Long memberId, @Param("socialId") String socialId);
}
