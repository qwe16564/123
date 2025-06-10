package com.example.jobmanagementsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private long totalCompanies;
    private long totalPositions;
    private long totalOpenPositions;
    private long totalResumes;
    private long totalApplications;
    private Map<String, Long> applicationCountByStatus;
}
