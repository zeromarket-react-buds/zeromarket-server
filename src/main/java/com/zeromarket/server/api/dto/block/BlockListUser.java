package com.zeromarket.server.api.dto.block;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockListUser {
    private Long blockId;
    private Long blockedUserId;     // 차단당한 유저
    private String profileImage;    // 차단당한 유저 이미지
    private String blockedUserNickname; // 차단 당한 유저 닉네임

    private boolean isActive;
}
