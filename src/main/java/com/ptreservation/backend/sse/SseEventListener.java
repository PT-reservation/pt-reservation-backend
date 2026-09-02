package com.ptreservation.backend.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseEventListener {

    private final SseEmitterRepository sseEmitterRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSeatUpdated(SeatUpdatedEvent event) {
        sseEmitterRepository.sendToClass(event.classId(), "seatUpdated", event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationPromoted(ReservationPromotedEvent event) {
        sseEmitterRepository.sendToMember(event.memberEmail(), "reservationPromoted", event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePromotionSkipped(PromotionSkippedEvent event) {
        sseEmitterRepository.sendToMember(event.memberEmail(), "promotionSkipped", event);
    }
}