package com.zeromarket.server.api.mapper.block;


import com.zeromarket.server.api.dto.block.BlockListUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlockMapper {
    String selectBlockerNickname(Long memberId);
    int countBlockedUsers(Long memberId);
    List<BlockListUser> selectBlockedUsers(Long memberId);

    Boolean selectIsBlocked(Long memberId, Long targetId);

    Boolean selectBlockIsActive(Long memberId, Long blockedUserId);

    Long selectBlockId(Long memberId, Long blockedUserId);

    int insertBlock(Long memberId, Long blockedUserId);

    int reactivateBlock(Long memberId, Long blockedUserId);

    int updateUnblock(Long blockId, Long memberId);
}
