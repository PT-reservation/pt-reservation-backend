package com.ptreservation.backend.controller;

import com.ptreservation.backend.common.ApiResponse;
import com.ptreservation.backend.dto.ClassRequest;
import com.ptreservation.backend.dto.ClassResponse;
import com.ptreservation.backend.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainers/me/classes")
@RequiredArgsConstructor
public class TrainerController {

    private final ClassService classService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(
            Authentication authentication,
            @Valid @RequestBody ClassRequest request
    ) {
        ClassResponse response = classService.createClass(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/{classId}")
    public ApiResponse<ClassResponse> updateClass(
            Authentication authentication,
            @PathVariable Long classId,
            @Valid @RequestBody ClassRequest request
    ) {
        return ApiResponse.ok(classService.updateClass(authentication.getName(), classId, request));
    }

    @DeleteMapping("/{classId}")
    public ApiResponse<Void> deleteClass(
            Authentication authentication,
            @PathVariable Long classId
    ) {
        classService.deleteClass(authentication.getName(), classId);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<List<ClassResponse>> getMyClasses(Authentication authentication) {
        return ApiResponse.ok(classService.getMyClasses(authentication.getName()));
    }
}