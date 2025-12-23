package com.zeromarket.server.api.dto.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockCreateResponse {
    private Long blockId;
    private String message;
}
