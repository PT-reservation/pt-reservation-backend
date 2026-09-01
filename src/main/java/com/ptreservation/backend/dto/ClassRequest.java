package com.ptreservation.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ClassRequest(
        @NotBlank String title,
        @NotNull @Future LocalDateTime classDateTime,
        @NotNull @Min(1) Integer capacity
) {
}