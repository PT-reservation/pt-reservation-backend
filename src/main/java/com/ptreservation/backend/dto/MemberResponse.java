package com.ptreservation.backend.dto;

import com.ptreservation.backend.domain.Member;

public record MemberResponse(
        Long id,
        String email,
        String name,
        String role
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getName(), member.getRole().name());
    }
}