package com.ptreservation.backend.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 로그인
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 클래스
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 클래스입니다."),
    NOT_CLASS_OWNER(HttpStatus.FORBIDDEN, "본인이 등록한 클래스만 수정/삭제할 수 있습니다."),

    // 예약
    NO_TICKET(HttpStatus.BAD_REQUEST, "사용 가능한 세션권이 없습니다."),
    ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예약한 클래스입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    NOT_RESERVATION_OWNER(HttpStatus.FORBIDDEN, "본인 예약만 취소할 수 있습니다."),
    RESERVATION_CONFLICT(HttpStatus.CONFLICT, "예약 요청이 몰려 처리하지 못했습니다. 다시 시도해주세요.");

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