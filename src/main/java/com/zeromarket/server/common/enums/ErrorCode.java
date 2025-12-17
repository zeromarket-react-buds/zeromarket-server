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
    JWT_NOT_EXIST(401, "refresh 토큰이 존재하지 않습니다."),
    JWT_NOT_VALID(401, "JWT 토큰이 유효하지 않습니다."),
    // Kakao OAuth 관련 (400번대)
    INVALID_AUTHORIZATION_CODE(400, "유효하지 않은 인증 코드입니다."),
    KAKAO_TOKEN_REQUEST_FAILED(400, "카카오 토큰 요청에 실패했습니다."),
    KAKAO_USER_INFO_REQUEST_FAILED(400, "카카오 사용자 정보 조회에 실패했습니다."),
    KAKAO_LOGIN_FAILED(400, "카카오 로그인에 실패했습니다."),
    KAKAO_UNLINK_FAILED(401, "카카오 회원 탈퇴에 실패했습니다"),

    // 회원
    MEMBER_NOT_FOUND(404, "회원 정보를 찾을 수 없습니다."),
    MEMBER_ALREADY_WITHDRAWN(404, "이미 탈퇴한 회원입니다."),

    // 시스템
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),

    // DB
    DB_INSERT_FAILED(500, "DB 내부 오류 (SQL 오류)"),
    DUPLICATE_RESOURCE(500, "중복된 정보가 존재합니다."),

    // 요청 json필드가 null
    INVALID_REQUEST(400,"잘못된 요청입니다."),

    // 상품
    PRODUCT_NOT_FOUND(404, "상품 정보를 찾을 수 없습니다."),
    DELETED_PRODUCT(410, "삭제된 상품입니다."),
    HIDDEN_PRODUCT(403, "숨김 처리된 상품입니다."),
    PRODUCT_SOLD_OUT(902, "이미 판매 완료된 상품입니다."),

    // 채팅
    CHAT_NOT_FOUND(404, "채팅 정보를 찾을 수 없습니다."),

    // 거래
    TRADE_NOT_FOUND(404, "거래 정보를 찾을 수 없습니다."),
    TRADE_CREATE_FAILED(901, "거래 정보를 생성하는 데 실패했습니다."),
    TRADE_ALREADY_EXIST(409, "거래 정보가 이미 존재합니다."),
    TRADE_PENDING_ALREADY_EXIST(409, "예약중인 거래가 이미 존재합니다."),
    TRADE_COMPLETED_ALREADY_EXIST(409, "이미 완료된 거래입니다."),
    TRADE_PROCESSING_ALREADY_EXIST(409, "이미 예약중이거나 거래가 완료된 상품입니다."),

    // 후기
    REVIEW_EXIST(409, "이미 리뷰를 작성한 거래입니다."),
    REVIEW_CREATE_FORBIDDEN(403, "거래에 참여한 사용자만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(404, "후기 정보를 찾을 수 없습니다.");



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

