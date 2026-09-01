package com.ptreservation.backend.controller;

import com.ptreservation.backend.common.ApiResponse;
import com.ptreservation.backend.dto.MemberResponse;
import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/members/me")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MemberResponse> getMyProfile(Authentication authentication) {
        return ApiResponse.ok(memberService.getMyProfile(authentication.getName()));
    }

    @GetMapping("/reservations")
    public ApiResponse<List<ReservationResponse>> getMyReservations(Authentication authentication) {
        return ApiResponse.ok(memberService.getMyReservations(authentication.getName()));
    }
}