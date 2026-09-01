package com.ptreservation.backend.controller;

import com.ptreservation.backend.common.ApiResponse;
import com.ptreservation.backend.dto.TicketChargeRequest;
import com.ptreservation.backend.dto.TicketResponse;
import com.ptreservation.backend.service.SessionTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/me/ticket")
@RequiredArgsConstructor
public class SessionTicketController {

    private final SessionTicketService sessionTicketService;

    @GetMapping
    public ApiResponse<TicketResponse> getMyTicket(Authentication authentication) {
        return ApiResponse.ok(sessionTicketService.getMyTicket(authentication.getName()));
    }

    @PostMapping("/charge")
    public ApiResponse<TicketResponse> charge(
            Authentication authentication,
            @Valid @RequestBody TicketChargeRequest request
    ) {
        return ApiResponse.ok(sessionTicketService.charge(authentication.getName(), request.count()));
    }
}