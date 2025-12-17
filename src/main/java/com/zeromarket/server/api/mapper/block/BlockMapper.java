package com.zeromarket.server.api.mapper.block;


import com.zeromarket.server.api.dto.block.BlockListUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlockMapper {
    String selectBlockerNickname(Long memberId);
    int countBlockedUsers(Long memberId);
    List<BlockListUser> selectBlockedUsers(Long memberId);
}
