package com.zeromarket.server.api.dto.block;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlockListResponse {
    private String nickname;          // 차단요청한 닉네임
    private Integer blockedUserCount; // 차단 수
    private List<BlockListUser> list; // 차단 유저 목록
}
