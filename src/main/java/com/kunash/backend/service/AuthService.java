package com.kunash.backend.service;

import com.kunash.backend.dto.request.LoginRequest;
import com.kunash.backend.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
}