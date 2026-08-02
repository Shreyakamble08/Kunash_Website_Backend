package com.kunash.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private AdminInfo admin;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfo {
        private String name;
        private String email;
        private String role;
    }
}