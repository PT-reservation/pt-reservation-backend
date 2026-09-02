package com.ptreservation.backend.sse;

public record PromotionSkippedEvent(String memberEmail, Long classId) {
}