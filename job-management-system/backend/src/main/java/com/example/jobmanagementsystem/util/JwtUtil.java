package com.example.jobmanagementsystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Consider externalizing these properties
    @Value("${jwt.secret:defaultSecretKeyWhichShouldBeLongAndSecureAnd至少32Bytes}") // Default for HS256, ensure it's strong
    private String secretKeyString;

    private Key secretKey;

    @Value("${jwt.expiration.ms:3600000}") // Default to 1 hour
    private long jwtExpirationInMs;

    // Initialize the key after properties are set
    @jakarta.annotation.PostConstruct
    private void init() {
        // Ensure the key is strong enough for HS256
        if (secretKeyString == null || secretKeyString.length() < 32) {
            // Fallback to a default secure key if not configured properly or too short
            // This is important for security. A real app should fail startup or use a robust default.
            this.secretKeyString = "FallbackSecureKeyMustBeAtLeast32BytesLongForHS256";
             // Log a warning in a real app
            System.err.println("Warning: JWT secret key is not configured or too short. Using a default fallback key. THIS IS NOT SECURE FOR PRODUCTION.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add any additional claims, e.g., roles
        // claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(String username) { // Overload for simple username
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
     public Boolean validateToken(String token, String username) { // Overload for simple username validation
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
