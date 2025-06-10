package com.example.jobmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;
    private Long resumeId;
    private String resumeFileName; // For convenience
    private Long positionId;
    private String positionTitle; // For convenience
    private Long applicantUserId;
    private String applicantUsername; // For convenience
    private Timestamp applicationDate;
    private String status;
    private String notes;
    private Timestamp updatedAt;
}
