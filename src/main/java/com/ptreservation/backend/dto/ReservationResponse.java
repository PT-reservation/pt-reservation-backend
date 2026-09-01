package com.ptreservation.backend.dto;

import com.ptreservation.backend.domain.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long classId,
        String classTitle,
        String status,
        LocalDateTime reservedAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getFitnessClass().getId(),
                reservation.getFitnessClass().getTitle(),
                reservation.getStatus().name(),
                reservation.getReservedAt()
        );
    }
}