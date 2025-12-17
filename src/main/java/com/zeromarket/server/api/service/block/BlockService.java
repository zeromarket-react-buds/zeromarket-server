package com.zeromarket.server.api.service.block;

import com.zeromarket.server.api.dto.block.BlockListResponse;

public interface BlockService {
    BlockListResponse getBlockList(Long memberId);
}
