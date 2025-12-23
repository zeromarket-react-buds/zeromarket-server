package com.zeromarket.server.api.mapper.noti;

import com.zeromarket.server.common.entity.KeywordAlert;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KeywordAlertMapper {

    List<KeywordAlert> selectKeywordAlertsByMember(@Param("memberId") Long memberId);

    void insertKeywordAlert(KeywordAlert keywordAlert);

    void updateKeywordAlert(KeywordAlert keywordAlert);

    void deleteKeywordAlert(Long alertId, Long memberId);

    KeywordAlert getKeywordAlertsByIds(Long alertId, Long memberId);
}
