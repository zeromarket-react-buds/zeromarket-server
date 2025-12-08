package com.zeromarket.server.api.mapper.report;

import com.zeromarket.server.api.dto.report.ReportCreateRequest;
import com.zeromarket.server.api.dto.report.ReportReasonCodeResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {
    //신고등록
    Long insertReport(
        @Param("reporterId") Long reporterId,
        @Param("request")ReportCreateRequest request
    );

    //중복신고여부확인
    int existsReport(
        @Param("reporterId") Long reporterId,
        @Param("targetType") String targetType,
        @Param("targetId") Long targetId
    );

    //신고사유들
    List<ReportReasonCodeResponse> getActiveReasons();
}
