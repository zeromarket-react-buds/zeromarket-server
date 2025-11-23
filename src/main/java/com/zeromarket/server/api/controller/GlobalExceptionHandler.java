package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.ErrorResponse;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleSignupFailures(RuntimeException e) {
        // HTTP 상태 코드를 401(인증 실패) 또는 400(잘못된 요청)으로 통일합니다.
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED) // 401
            .body(new ErrorResponse("회원가입 실패", e.getMessage()));
    }

    @ExceptionHandler({NoSuchElementException.class, SecurityException.class})
    public ResponseEntity<ErrorResponse> handleLoginFailures(RuntimeException e) {
        // HTTP 상태 코드를 401(인증 실패) 또는 400(잘못된 요청)으로 통일합니다.
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED) // 401
            .body(new ErrorResponse("로그인 실패", e.getMessage()));
    }

}
