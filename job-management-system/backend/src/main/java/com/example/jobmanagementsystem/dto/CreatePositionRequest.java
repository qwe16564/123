package com.example.jobmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePositionRequest {
    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description is required")
    private String description; // TEXT type, size validation might be less critical here

    private String requirements; // TEXT type

    @Size(max = 255)
    private String location;

    @Size(max = 50)
    private String employmentType; // e.g., 'Full-time', 'Part-time', 'Contract'

    // Status typically defaults to 'Open', not set at creation by user directly
    // private String status;

    @NotNull(message = "Poster user ID is required")
    private Long postedByUserId;
}
