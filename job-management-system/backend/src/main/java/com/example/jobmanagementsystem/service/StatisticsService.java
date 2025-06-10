package com.example.jobmanagementsystem.service;

import java.util.Map;

public interface StatisticsService {
    long getTotalCompanies();
    long getTotalPositions();
    long getTotalOpenPositions();
    long getTotalResumes();
    long getTotalApplications();
    Map<String, Long> getApplicationCountByStatus();
}
