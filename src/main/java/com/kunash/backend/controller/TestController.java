package com.kunash.backend.controller;

import com.kunash.backend.dto.response.ApiResponse;
import com.kunash.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/public")
    public ApiResponse<String> publicEndpoint() {
        return ApiResponse.success("This is a public endpoint - no authentication needed!");
    }

    @GetMapping("/protected")
    public ApiResponse<Map<String, Object>> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Map<String, Object> data = new HashMap<>();
        data.put("message", "You are authenticated!");
        data.put("user", username);
        data.put("authorities", auth.getAuthorities());

        return ApiResponse.success("Access granted to protected endpoint", data);
    }

    @GetMapping("/admin-only")
    public ApiResponse<String> adminOnlyEndpoint() {
        return ApiResponse.success("Welcome Admin! You have special privileges.");
    }

    // ========== EMAIL TEST ENDPOINT ==========
    @GetMapping("/email")
    public ApiResponse<String> testEmail(@RequestParam String to) {
        try {
            emailService.sendEmail(
                    to,
                    "Test Email from Kunash Backend",
                    "<h1>✅ Test Email</h1><p>If you received this, email is working!</p>"
            );
            return ApiResponse.success("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            return ApiResponse.error("❌ Failed to send email: " + e.getMessage());
        }
    }
}