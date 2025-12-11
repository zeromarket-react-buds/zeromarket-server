package com.zeromarket.server.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String description;
    private String trustScore; // 서비스에서 주입
    private boolean liked; // 서비스에서 주입
}

