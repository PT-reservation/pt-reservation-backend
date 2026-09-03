package com.ptreservation.backend.dto;

import com.ptreservation.backend.domain.FitnessClass;

import java.time.LocalDateTime;

public record ClassResponse(
        Long id,
        String title,
        String trainerName,
        LocalDateTime classDateTime,
        int capacity,
        int currentCount,
        String description,
        String imageUrl
) {
    public static ClassResponse from(FitnessClass fitnessClass) {
        return new ClassResponse(
                fitnessClass.getId(),
                fitnessClass.getTitle(),
                fitnessClass.getTrainer().getName(),
                fitnessClass.getClassDateTime(),
                fitnessClass.getCapacity(),
                fitnessClass.getCurrentCount(),
                fitnessClass.getDescription(),
                fitnessClass.getImageUrl()
        );
    }
}