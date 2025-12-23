package com.zeromarket.server.api.controller.report;

import com.zeromarket.server.api.dto.report.ReportCreateRequest;
import com.zeromarket.server.api.dto.report.ReportCreateResponse;
import com.zeromarket.server.api.dto.report.ReportReasonCodeResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.report.ReportCommandService;
import com.zeromarket.server.api.service.report.ReportQueryService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "신고 API", description = "신고 관련 API")
public class ReportRestController {

    private final ReportCommandService reportCommandService;
    private final ReportQueryService reportQueryService;

    @Operation(summary = "신고 접수", description = "신고 접수하기")
    @PostMapping
    public ResponseEntity<ReportCreateResponse> createReport(
        @RequestBody ReportCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        if(userDetails==null){
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Long reporterId = userDetails.getMemberId();
        Long reportId = reportCommandService.createReport(reporterId,request);

        if(reportId==null){ // 중복이라 서비스에서 insert 하지않음
            return ResponseEntity.ok(
                new ReportCreateResponse(null,"이미 신고한 게시글입니다.")
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ReportCreateResponse(reportId,"신고가 정상적으로 접수되었습니다."));
    }

    @Operation(summary = "신고 사유 조회", description = "신고 사유 모달창에 불러오기")
    @GetMapping("/reasons")
    public List<ReportReasonCodeResponse> getReportReasons(
        @RequestParam("targetType") String targetType //신고사유테이블의 targetType컬럼
    ){
        return reportQueryService.getActiveReasons(targetType);
    }



}
