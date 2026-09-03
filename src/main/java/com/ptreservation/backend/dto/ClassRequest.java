package com.ptreservation.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ClassRequest(
        @NotBlank String title,
        @NotNull @Future LocalDateTime classDateTime,
        @NotNull @Min(1) Integer capacity,
        @NotBlank @Size(max = 1000) String description,
        @Size(max = 500) String imageUrl
) {
}