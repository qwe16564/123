package com.example.jobmanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Apply CORS to all API v1 endpoints
            .allowedOrigins(
                "http://localhost:5173", // Default Vite dev port
                "http://127.0.0.1:5173",
                "http://localhost:8081", // Common port for vue-cli or other setups
                "http://127.0.0.1:8081"
                // Add other frontend origins if necessary (e.g., production URL later)
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Important for cookies, authorization headers with HTTPS
            .maxAge(3600); // Cache pre-flight response for 1 hour
    }
}
