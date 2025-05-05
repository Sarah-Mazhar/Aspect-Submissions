package com.example.demo.lab5.controller;

import com.example.lab5.dto.JwtResponse;
import com.example.lab5.dto.LoginRequest;
import com.example.lab5.dto.RegisterRequest;
import com.example.lab5.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.loginUser(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }
}