package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.ErrorResponse;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {

        ErrorCode code = e.getErrorCode();

        return ResponseEntity
            .status(code.getStatus())
            .body(new ErrorResponse(code.name(), code.getMessage()));
    }

//    TODO: e.getMessage()는 log.error 등으로 서버 로그에서만 노출
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
            .status(500)
            .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
//        log.error("예외 발생 ######## ", e.getMessage());
//        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
//
//        return ResponseEntity
//            .status(code.getStatus())
//            .body(new ErrorResponse(code.name(), code.getMessage()));
    }

}
