package com.example.jobmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private String industry;
    private String website;
    private String address;
    private Long createdByUserId; // ID of the user who created the company
    private String createdByUsername; // Username of the user
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
