package com.ptreservation.backend.controller;

import com.ptreservation.backend.common.ApiResponse;
import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/classes/{classId}/reservations")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserve(
            Authentication authentication,
            @PathVariable Long classId
    ) {
        ReservationResponse response = reservationService.reserve(authentication.getName(), classId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ApiResponse<Void> cancel(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        reservationService.cancel(authentication.getName(), reservationId);
        return ApiResponse.ok();
    }
}