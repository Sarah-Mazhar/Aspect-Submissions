package com.example.demo.lab5.service;

import com.example.lab5.dto.JwtResponse;
import com.example.lab5.dto.LoginRequest;
import com.example.lab5.dto.RegisterRequest;
import com.example.lab5.entity.User;
import com.example.lab5.repository.UserRepository;
import com.example.lab5.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        User user = new User(registerRequest.getUsername(),
                encoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<JwtResponse> loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        JwtResponse jwtResponse = new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername());

        return ResponseEntity.ok(jwtResponse);
    }
}
