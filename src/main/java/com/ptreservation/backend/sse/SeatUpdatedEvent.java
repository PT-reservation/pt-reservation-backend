package com.ptreservation.backend.sse;

public record SeatUpdatedEvent(Long classId, int currentCount, int capacity) {
}