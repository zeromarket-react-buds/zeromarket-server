package com.zeromarket.server.api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportCreateResponse {
    private Long reportId;
    private String message;

}
