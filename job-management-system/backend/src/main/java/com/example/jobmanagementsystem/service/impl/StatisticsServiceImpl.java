package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.entity.Application;
import com.example.jobmanagementsystem.repository.*;
import com.example.jobmanagementsystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // Statistics are read-only operations
public class StatisticsServiceImpl implements StatisticsService {

    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public StatisticsServiceImpl(CompanyRepository companyRepository,
                                 PositionRepository positionRepository,
                                 ResumeRepository resumeRepository,
                                 ApplicationRepository applicationRepository) {
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.resumeRepository = resumeRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public long getTotalCompanies() {
        return companyRepository.count();
    }

    @Override
    public long getTotalPositions() {
        return positionRepository.count();
    }

    @Override
    public long getTotalOpenPositions() {
        // Assuming 'Open' is the status string for open positions.
        // This could be made more robust with an enum or constant.
        return positionRepository.countByStatus("Open");
    }

    @Override
    public long getTotalResumes() {
        return resumeRepository.count();
    }

    @Override
    public long getTotalApplications() {
        return applicationRepository.count();
    }

    @Override
    public Map<String, Long> getApplicationCountByStatus() {
        List<Application> applications = applicationRepository.findAll();
        return applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));
    }
}
