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
public class CreateApplicationRequest {
    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    @NotNull(message = "Position ID is required")
    private Long positionId;

    @NotNull(message = "Applicant User ID is required")
    private Long applicantUserId;

    @NotBlank(message = "Initial status is required")
    @Size(max = 50)
    private String status; // e.g., 'Submitted'

    private String notes; // Optional
}
