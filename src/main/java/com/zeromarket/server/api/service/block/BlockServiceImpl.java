package com.zeromarket.server.api.service.block;

import com.zeromarket.server.api.dto.block.BlockListResponse;
import com.zeromarket.server.api.mapper.block.BlockMapper;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockMapper mapper;

    // 차단 유저 목록페이지 목록 조회용
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

    // 차단 유저 목록페이지 차단 해제
    @Override
    public void updateUnblock(Long blockId, Long memberId) {
        int updated = mapper.updateUnblock(blockId, memberId);

        if (updated == 0) { // blockId가 존재하지 않거나 blocker_user_id ≠ memberId, 이미 is_active가 false인 경우
            throw new IllegalArgumentException("차단 정보가 없거나 권한이 없습니다.");
        }
    }

    // 차단한 유저인지 조회용
    @Override
    public boolean isBlocked(Long memberId, Long targetId) {
        if (targetId == null || memberId == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
        if (memberId.equals(targetId)) {
            return false;
        }

        // exists면 true/false로 바로 반환
        Boolean result = mapper.selectIsBlocked(memberId, targetId);
        return Boolean.TRUE.equals(result);
    }

    // 차단 등록
    @Transactional
    public Long createBlock(Long memberId, Long blockedUserId) {

        if (blockedUserId == null || memberId.equals(blockedUserId)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        // 기존 차단 여부 조회 (없으면 null)
        Boolean isActive = mapper.selectBlockIsActive(memberId, blockedUserId);

        // 처음 차단 (row 자체가 없음)
        if (isActive == null) {
            int inserted = mapper.insertBlock(memberId, blockedUserId);
            if (inserted == 0) {
                throw new ApiException(ErrorCode.DB_INSERT_FAILED);
            }

            Long blockId = mapper.selectBlockId(memberId, blockedUserId);
            if (blockId == null) {
                throw new ApiException(ErrorCode.DB_INSERT_FAILED);
            }

            return blockId;
        }

        // 재차단 (is_active = false를 true로)
        if (!isActive) {
            int updated = mapper.reactivateBlock(memberId, blockedUserId);
            if (updated == 0) {
                throw new ApiException(ErrorCode.DB_INSERT_FAILED);
            }

            return mapper.selectBlockId(memberId, blockedUserId);
        }

        // 이미 차단 상태
        return mapper.selectBlockId(memberId, blockedUserId);
    }


}
