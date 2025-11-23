package com.zeromarket.server.api.mapper;

import com.zeromarket.server.common.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    // 회원 생성
    int insertMember(Member member);

    // 회원 조회
    Member selectMemberById(Long memberId);
    Member selectMemberByLoginId(String loginId);
//    Optional<Member> selectMemberByEmail(String email);
//    Optional<Member> selectMemberByNickname(String nickname);
//
//    // 회원 목록
//    List<Member> selectMembers(MemberSearchRequest searchRequest);
//    int countMembers(MemberSearchRequest searchRequest);
//
//    // 회원 수정
//    void updateMember(Member member);
//
//    // 비밀번호 변경
//    void updatePassword(@Param("memberId") Long memberId, @Param("password") String password);
//
//    // 회원 탈퇴
//    void withdrawMember(@Param("memberId") Long memberId,
//        @Param("withdrawalReasonId") Long withdrawalReasonId,
//        @Param("withdrawalReasonDetail") String withdrawalReasonDetail);
//
//    // 중복 체크
    boolean existsByLoginId(String loginId);
//    boolean existsByEmail(String email);
//    boolean existsByNickname(String nickname);
//    boolean existsByPhone(String phone);
}
