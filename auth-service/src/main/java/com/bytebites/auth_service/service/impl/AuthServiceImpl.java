package com.bytebites.auth_service.service.impl;

import com.bytebites.auth_service.dto.LoginRequest;
import com.bytebites.auth_service.dto.LoginResponse;
import com.bytebites.auth_service.dto.RegisterRequest;
import com.bytebites.auth_service.model.Role;
import com.bytebites.auth_service.model.User;
import com.bytebites.auth_service.repository.UserRepository;
import com.bytebites.auth_service.security.CustomUserDetails;
import com.bytebites.auth_service.security.JwtService;
import com.bytebites.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link AuthService} for handling local user registration and authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) throws BadRequestException {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }

        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.ROLE_CUSTOMER)
                .build();

       userRepository.save(newUser);
       log.info("User {} registered successfully.", newUser.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return new LoginResponse(
                    token,
                    userDetails.getUsername(),
                    userDetails.getUser().getRole()
            );
    }
}