package com.bytebites.auth_service.service;

import com.bytebites.auth_service.dto.LoginResponse;
import com.bytebites.auth_service.dto.LoginRequest;
import com.bytebites.auth_service.dto.RegisterRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    void register(RegisterRequest request) throws BadRequestException;

    LoginResponse login(LoginRequest request);
}
