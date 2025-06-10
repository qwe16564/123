package com.example.jobmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.URL;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be less than 255 characters")
    private String name;

    private String description; // Optional

    @Size(max = 255, message = "Industry must be less than 255 characters")
    private String industry; // Optional

    @Size(max = 255, message = "Website URL too long")
    @URL(message = "Website should be a valid URL")
    private String website; // Optional

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address; // Optional

    @NotNull(message = "Creator user ID is required")
    private Long createdByUserId;
}
