package com.zeromarket.server.api.dto.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockStatusResponse {
    private boolean isBlocked;
}
