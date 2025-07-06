package com.bytebites.auth_service.dto;

import com.bytebites.auth_service.model.Role;

public record LoginResponse(
        String token,
        String email,
        Role role
) {}