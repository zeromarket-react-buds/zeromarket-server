package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.common.entity.KeywordAlert;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface KeywordAlertService {

    List<KeywordAlert> selectKeywordAlertsByMember(Long memberId);

    void insertKeywordAlert(KeywordAlert keywordAlert);

    void updateKeywordAlert(KeywordAlert keywordAlert);

    void deleteKeywordAlert(Long alertId, Long memberId);

    KeywordAlert getKeywordAlertsByIds(Long alertId, Long memberId);
}
