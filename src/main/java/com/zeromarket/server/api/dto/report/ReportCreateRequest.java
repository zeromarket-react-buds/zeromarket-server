package com.zeromarket.server.api.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequest {
    private Long reportId; //insert 후 mybatis가 채워줌

    private Long reasonId;
    private String targetType; //신고 대상 유형(MEMBER/PRODUCT)
    private Long targetId; //신고대상id(productId or memberId
    private String reasonText; //기타사유 입력시 -reasonId가 ETC일때만

}
