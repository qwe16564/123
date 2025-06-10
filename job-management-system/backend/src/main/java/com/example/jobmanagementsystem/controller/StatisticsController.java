package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.StatisticsDto;
import com.example.jobmanagementsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<StatisticsDto> getStatisticsSummary() {
        long totalCompanies = statisticsService.getTotalCompanies();
        long totalPositions = statisticsService.getTotalPositions();
        long totalOpenPositions = statisticsService.getTotalOpenPositions();
        long totalResumes = statisticsService.getTotalResumes();
        long totalApplications = statisticsService.getTotalApplications();
        Map<String, Long> appCountByStatus = statisticsService.getApplicationCountByStatus();

        StatisticsDto dto = new StatisticsDto(
                totalCompanies,
                totalPositions,
                totalOpenPositions,
                totalResumes,
                totalApplications,
                appCountByStatus
        );
        return ResponseEntity.ok(dto);
    }
}
