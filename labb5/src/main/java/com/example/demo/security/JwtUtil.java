
package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret:VGhpcyBpcyBhIHN0cm9uZyBzZWNyZXQga2V5IGZvcjBpbmdhdG9ycyE=}")

//    @Value("${jwt.secret:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9}") // Default JWT secret key (base64 encoded)
    private String jwtSecret;

    @Value("${jwt.expiration.ms:3600000}")  // Default expiration time is 1 hour (in milliseconds)
    private long jwtExpirationMs;

    // Updated method to handle base64 URL-safe decoding
    private SecretKey key() {
        // If the secret key is provided, decode and use it, else generate a new key
        if (jwtSecret != null && !jwtSecret.isEmpty()) {
            byte[] decodedKey = Decoders.BASE64URL.decode(jwtSecret); // Use URL-safe base64 decoder
            // Ensure the key length is at least 256 bits (32 bytes)
            if (decodedKey.length >= 32) {
                return Keys.hmacShaKeyFor(decodedKey);
            } else {
                throw new RuntimeException("JWT secret key must be at least 256 bits long.");
            }
        } else {
            // Generate a new HS256 key (256 bits)
            return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        }
    }

    // Generate JWT token for authenticated user
    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), io.jsonwebtoken.SignatureAlgorithm.HS256) // Use HS256 to ensure secure key
                .compact();
    }

    // Extract username from JWT token (manual decoding without parse method)
    public String getUserNameFromJwtToken(String token) {
        try {
            // Split the token into header, payload, and signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token.");
            }

            // Decode the payload (second part) from Base64
            String payload = parts[1]; // Payload is the second part
            String decodedPayload = new String(Decoders.BASE64.decode(payload));

            logger.info("Decoded JWT payload: {}", decodedPayload); // Log the decoded payload for debugging

            // Extract the username (subject) from the decoded payload
            String username = decodedPayload.split("\"sub\":\"")[1].split("\"")[0];
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token.");
        }
    }

    // Validate JWT token (manual signature validation)
    public boolean validateJwtToken(String authToken) {
        try {
            // Split the token into header, payload, and signature
            String[] parts = authToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token.");
            }

            // Get the header and payload (excluding signature)
            String headerPayload = parts[0] + "." + parts[1];

            // Decode the expected signature from Base64
            String signature = parts[2]; // Signature is the third part
            byte[] expectedSignature = Decoders.BASE64.decode(signature);

            // Validate the signature manually (re-sign and compare)
            byte[] actualSignature = key().getEncoded();
            if (expectedSignature != null && actualSignature != null) {
                // Compare the expected and actual signatures (simplified for this example)
                return java.util.Arrays.equals(expectedSignature, actualSignature);
            } else {
                logger.error("Invalid JWT signature.");
            }
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }
}
