package com.ptreservation.backend.controller;

import com.ptreservation.backend.common.ApiResponse;
import com.ptreservation.backend.dto.ClassResponse;
import com.ptreservation.backend.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    public ApiResponse<List<ClassResponse>> getClasses() {
        return ApiResponse.ok(classService.getClasses());
    }

    @GetMapping("/{classId}")
    public ApiResponse<ClassResponse> getClass(@PathVariable Long classId) {
        return ApiResponse.ok(classService.getClass(classId));
    }
}