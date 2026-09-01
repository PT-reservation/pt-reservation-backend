package com.ptreservation.backend.sse;

public record ReservationPromotedEvent(String memberEmail, Long classId) {
}