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
public class CreateResumeRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "File path is required") // This will change with actual file upload
    @Size(max = 255)
    private String filePath;

    @Size(max = 255)
    private String originalFileName;

    private String contentText; // Optional, can be extracted by backend
}
