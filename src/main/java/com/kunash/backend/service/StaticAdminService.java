package com.kunash.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class StaticAdminService {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    private String encodedPassword;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        this.encodedPassword = passwordEncoder.encode(adminPassword);
        log.info("✅ Static Admin Initialized:");
        log.info("   📧 Email: {}", adminEmail);
        log.info("   👤 Name: {}", adminName);
    }

    public boolean validateCredentials(String email, String password) {
        if (!adminEmail.equals(email)) {
            log.warn("Invalid email attempt: {}", email);
            return false;
        }

        boolean isValid = passwordEncoder.matches(password, encodedPassword);

        if (isValid) {
            log.info("✅ Admin login successful: {}", email);
        } else {
            log.warn("❌ Invalid password attempt for: {}", email);
        }

        return isValid;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }
}