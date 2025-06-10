package com.example.jobmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto {
    private Long id;
    private Long companyId;
    private String companyName; // For convenience
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String employmentType;
    private String status;
    private Long postedByUserId;
    private String postedByUsername; // For convenience
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
