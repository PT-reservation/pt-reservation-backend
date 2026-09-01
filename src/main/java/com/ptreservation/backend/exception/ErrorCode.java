package com.ptreservation.backend.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 로그인
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 클래스
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 클래스입니다."),
    NOT_CLASS_OWNER(HttpStatus.FORBIDDEN, "본인이 등록한 클래스만 수정/삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}