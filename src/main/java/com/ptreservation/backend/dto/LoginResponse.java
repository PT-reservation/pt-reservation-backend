package com.ptreservation.backend.dto;

public record LoginResponse(
        String token,
        Long memberId,
        String name,
        String role
) {
}