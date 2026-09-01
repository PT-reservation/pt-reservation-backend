package com.ptreservation.backend.dto;

import com.ptreservation.backend.domain.SessionTicket;

public record TicketResponse(
        int totalCount,
        int remainingCount
) {
    public static TicketResponse from(SessionTicket ticket) {
        return new TicketResponse(ticket.getTotalCount(), ticket.getRemainingCount());
    }
}