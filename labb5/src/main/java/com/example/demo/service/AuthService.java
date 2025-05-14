package com.example.demo.service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
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

    @Autowired
    private PasswordEncoder passwordEncoder;


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

//    public ResponseEntity<JwtResponse> loginUser(LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtil.generateJwtToken(authentication);
//
//        User userDetails = (User) authentication.getPrincipal();
//
//        JwtResponse jwtResponse = new JwtResponse(jwt,
//                userDetails.getId(),
//                userDetails.getUsername());
//
//        return ResponseEntity.ok(jwtResponse);
//    }


    public ResponseEntity<JwtResponse> loginUser(LoginRequest loginRequest) {
        // Retrieve user from the database
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare the password using BCryptPasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials"); // Handle invalid credentials
        }

        // Authenticate the user (now that we know the password matches)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authentication in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the JWT token
        String jwt = jwtUtil.generateJwtToken(authentication);

        // Get the UserDetails (Principal) from the authentication
        User userDetails = (User) authentication.getPrincipal();

        // Return the JWT response
        JwtResponse jwtResponse = new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername());

        return ResponseEntity.ok(jwtResponse);
    }

}
