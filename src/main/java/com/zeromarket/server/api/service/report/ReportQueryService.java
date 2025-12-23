package com.zeromarket.server.api.service.report;

import com.zeromarket.server.api.dto.report.ReportReasonCodeResponse;
import java.util.List;

public interface ReportQueryService {
    List<ReportReasonCodeResponse> getActiveReasons(String targetType);
}
