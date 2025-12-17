package com.zeromarket.server.api.service.block;

import com.zeromarket.server.api.dto.block.BlockListResponse;
import com.zeromarket.server.api.mapper.block.BlockMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockMapper mapper;

    @Override
    public BlockListResponse getBlockList(Long memberId) {
        BlockListResponse res = new BlockListResponse();
        // 차단 요청한 사람 닉네임용
        res.setNickname(mapper.selectBlockerNickname(memberId));
        // 차단당한 사람 수
        res.setBlockedUserCount(mapper.countBlockedUsers(memberId));
        // 차단당한 유저 리스트
        res.setList(mapper.selectBlockedUsers(memberId));
        
        return res;
    }
}
