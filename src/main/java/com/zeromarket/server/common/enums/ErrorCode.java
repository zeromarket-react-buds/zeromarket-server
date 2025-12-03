package com.zeromarket.server.common.enums;

// ✅ 권장 에러 코드 설계 원칙
// 1. 계층 구조 형태로 만든다
// [도메인]_[행위]_[원인]

public enum ErrorCode {

    // 인증 관련
    UNAUTHORIZED(401, "로그인이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    LOGINID_ALREADY_EXIST(409, "로그인 ID가 이미 존재합니다."),
    NICKNAME_ALREADY_EXIST(409, "닉네임이 이미 존재합니다."),
    PHONE_ALREADY_EXIST(409, "핸드폰 번호가 이미 존재합니다."),
    EMAIL_ALREADY_EXIST(409, "이메일이 이미 존재합니다."),
    LOGIN_FAIL(401, "아이디 또는 비밀번호가 올바르지 않습니다."),

    // 회원
    MEMBER_NOT_FOUND(404, "회원 정보를 찾을 수 없습니다."),

    // 시스템
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),

    // DB
    DB_INSERT_FAILED(500, "DB 내부 오류 (SQL 오류)"),
    DUPLICATE_RESOURCE(500, "중복된 정보가 존재합니다."), 
    
    // 상품
    PRODUCT_NOT_FOUND(404, "상품 정보를 찾을 수 없습니다."),
    DELETED_PRODUCT(410, "삭제된 상품입니다."),
    HIDDEN_PRODUCT(403, "숨김 처리된 상품입니다."),

    // 채팅
    CHAT_NOT_FOUND(404, "채팅 정보를 찾을 수 없습니다."),

    // 거래
    TRADE_NOT_FOUND(404, "거래 정보를 찾을 수 없습니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

