package com.project.billingManagementSystem.securityapp.controller;

import com.project.billingManagementSystem.securityapp.entity.Users;
import com.project.billingManagementSystem.securityapp.entity.dto.LoginRequest;
import com.project.billingManagementSystem.securityapp.entity.dto.UserRequest;
import com.project.billingManagementSystem.securityapp.entity.responseDTO.LoginResponse;
import com.project.billingManagementSystem.securityapp.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRequest request) {

        try {

            Users user =
                    authService.register(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message",
                            "User registered successfully",

                            "username",
                            user.getUsername(),

                            "role",
                            user.getRole()
                    ));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {

            String token =
                    authService.login(request);

            LoginResponse response =
                    new LoginResponse(
                            "Login successful",
                            request.getUsername(),
                            token
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Invalid username or password"
                    ));
        }
    }
}
