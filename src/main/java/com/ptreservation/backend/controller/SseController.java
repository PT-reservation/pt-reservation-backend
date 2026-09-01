package com.ptreservation.backend.controller;

import com.ptreservation.backend.sse.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterRepository sseEmitterRepository;

    @GetMapping("/classes/{classId}/events")
    public SseEmitter subscribeToClass(@PathVariable Long classId) {
        return sseEmitterRepository.subscribeToClass(classId);
    }

    @GetMapping("/notifications/events")
    public SseEmitter subscribeToNotifications(Authentication authentication) {
        return sseEmitterRepository.subscribeToMember(authentication.getName());
    }
}