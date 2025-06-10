package com.example.jobmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDto {
    private Long id;
    private Long userId;
    private String username; // For convenience
    private String filePath;
    private String originalFileName;
    private String contentText; // Optional, if extracted
    private Timestamp uploadedAt;
    private Timestamp updatedAt;
}
