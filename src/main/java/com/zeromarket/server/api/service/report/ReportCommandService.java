package com.zeromarket.server.api.service.report;

import com.zeromarket.server.api.dto.report.ReportCreateRequest;

public interface ReportCommandService {

    Long createReport(Long reporterId, ReportCreateRequest request);
}
