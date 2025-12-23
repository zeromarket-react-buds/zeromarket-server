package com.zeromarket.server.api.dto.block;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockCreateRequest {
    private Long blockedUserId;
}
