package com.ptreservation.backend.dto;

import com.ptreservation.backend.domain.Reservation;

import java.time.LocalDateTime;

public record ClassReservationResponse(
        Long id,
        String memberName,
        String memberEmail,
        String status,
        LocalDateTime reservedAt
) {
    public static ClassReservationResponse from(Reservation reservation) {
        return new ClassReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getMember().getEmail(),
                reservation.getStatus().name(),
                reservation.getReservedAt()
        );
    }
}