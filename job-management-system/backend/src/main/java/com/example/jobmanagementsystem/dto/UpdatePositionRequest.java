package com.example.jobmanagementsystem.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePositionRequest {
    @Size(max = 255)
    private String title; // Optional

    private String description; // Optional

    private String requirements; // Optional

    @Size(max = 255)
    private String location; // Optional

    @Size(max = 50)
    private String employmentType; // Optional

    @Size(max = 50)
    private String status; // Optional (e.g., 'Open', 'Closed', 'Filled')

    // companyId and postedByUserId are typically not updatable via this DTO
}
