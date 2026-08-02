package com.kunash.backend.service.impl;

import com.kunash.backend.dto.request.LoginRequest;
import com.kunash.backend.dto.response.LoginResponse;
import com.kunash.backend.exception.InvalidCredentialsException;  // ✅ Import this
import com.kunash.backend.security.JwtTokenProvider;
import com.kunash.backend.service.AuthService;
import com.kunash.backend.service.StaticAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final StaticAdminService staticAdminService;
    private final JwtTokenProvider tokenProvider;

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        boolean isValid = staticAdminService.validateCredentials(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (!isValid) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        LoginResponse.AdminInfo adminInfo = new LoginResponse.AdminInfo(
                staticAdminService.getAdminName(),
                staticAdminService.getAdminEmail(),
                "ADMIN"
        );

        log.info("✅ Admin login successful: {}", loginRequest.getEmail());
        return new LoginResponse(jwt, "Bearer", adminInfo);
    }
}