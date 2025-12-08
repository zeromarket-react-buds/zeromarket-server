package com.zeromarket.server.api.service.report;

import com.zeromarket.server.api.dto.report.ReportReasonCodeResponse;
import com.zeromarket.server.api.mapper.report.ReportMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportQueryServiceImpl implements ReportQueryService{

    private final ReportMapper reportMapper;

    @Override
    public List<ReportReasonCodeResponse> getActiveReasons() {
        return reportMapper.getActiveReasons();
    }
}
