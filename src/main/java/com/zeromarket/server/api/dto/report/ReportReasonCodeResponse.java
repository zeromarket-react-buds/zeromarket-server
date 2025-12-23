package com.zeromarket.server.api.dto.report;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportReasonCodeResponse {
    private Long reasonId;
    private String reasonCode;
    private String reasonDescription;
    private Boolean isActive;
}
