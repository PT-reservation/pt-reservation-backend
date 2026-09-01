package com.ptreservation.backend.common;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }
}