package com.example.jobmanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // Optional: only update if provided

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password; // Optional

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email; // Optional

    @Size(max = 50, message = "Role must be less than 50 characters")
    private String role; // Optional
}
