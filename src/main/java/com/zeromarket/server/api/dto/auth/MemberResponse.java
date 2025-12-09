package com.zeromarket.server.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String loginId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String introduction;
    private String role;
    private String profileImage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
//    public static MemberResponse from(Member member) {
//        return MemberResponse.builder()
//            .memberId(member.getMemberId())
//            .loginId(member.getLoginId())
//            .name(member.getName())
//            .nickname(member.getNickname())
//            .email(member.getEmail())
//            .phone(member.getPhone())
//            .introduction(member.getIntroduction())
//            .role(member.getRole())
//            .createdAt(member.getCreatedAt())
//            .updatedAt(member.getUpdatedAt())
//            .build();
//    }
}
