package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.api.mapper.noti.KeywordAlertMapper;
import com.zeromarket.server.common.entity.KeywordAlert;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordAlertServiceImpl implements KeywordAlertService {

    private final KeywordAlertMapper keywordAlertMapper;

    @Override
    public List<KeywordAlert> selectKeywordAlertsByMember(Long memberId) {
        return keywordAlertMapper.selectKeywordAlertsByMember(memberId);
    }

    @Override
    public void insertKeywordAlert(KeywordAlert keywordAlert) {
        keywordAlertMapper.insertKeywordAlert(keywordAlert);
    }

    @Override
    public void updateKeywordAlert(KeywordAlert keywordAlert) {
        keywordAlertMapper.updateKeywordAlert(keywordAlert);
    }

    @Override
    public void deleteKeywordAlert(Long alertId, Long memberId) {
        keywordAlertMapper.deleteKeywordAlert(alertId, memberId);
    }

    @Override
    public KeywordAlert getKeywordAlertsByIds(Long alertId, Long memberId) {
        return keywordAlertMapper.getKeywordAlertsByIds(alertId, memberId);
    }


}
