package com.zeromarket.server.common.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private Long memberId;
    private Long withdrawalReasonId;
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String introduction;
    private String withdrawalReasonDetail;
    private String role; // USER, ADMIN
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime withdrawnAt;
    private String socialId;
}
