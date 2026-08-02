package com.kunash.backend.controller;

import com.kunash.backend.dto.request.LoginRequest;
import com.kunash.backend.dto.response.ApiResponse;
import com.kunash.backend.dto.response.LoginResponse;
import com.kunash.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("🔐 Login attempt: {}", loginRequest.getEmail());
        LoginResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Service is running!"));
    }
}