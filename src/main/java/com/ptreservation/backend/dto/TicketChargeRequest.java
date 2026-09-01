package com.ptreservation.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TicketChargeRequest(
        @NotNull @Min(1) Integer count
) {
}