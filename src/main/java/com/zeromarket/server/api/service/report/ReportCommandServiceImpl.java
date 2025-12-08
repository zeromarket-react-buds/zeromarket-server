package com.zeromarket.server.api.service.report;

import com.zeromarket.server.api.dto.report.ReportCreateRequest;
import com.zeromarket.server.api.mapper.report.ReportMapper;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportCommandServiceImpl implements ReportCommandService {

    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public Long createReport(Long reporterId, ReportCreateRequest request) {
        if(reporterId==null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

//        필드 유효성 검증
        if(request.getTargetType()==null ||
            request.getTargetId()==null ||
            request.getReasonId()==null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }

        //중복신고방지
        int exists = reportMapper.existsReport(
            reporterId,
            request.getTargetType(),
            request.getTargetId()
        );

        if (exists > 0) {
            return null;
        }

        Long reportId = reportMapper.insertReport(reporterId,request);

        if(reportId==null){
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return reportId;
    }
}
