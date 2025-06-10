package com.example.jobmanagementsystem.service;

import com.example.jobmanagementsystem.entity.Application;
import java.util.List;

public interface ApplicationService {
    Application createApplication(Application application, Long resumeId, Long positionId, Long applicantUserId);
    Application getApplicationById(Long id);
    List<Application> getApplicationsByApplicantId(Long applicantUserId);
    List<Application> getApplicationsByPositionId(Long positionId);
    List<Application> getApplicationsByApplicantId(Long applicantUserId); // Already exists, can be used for "my-applications" by passing current user's ID
    Application getApplicationByPositionAndApplicant(Long positionId, Long applicantUserId); // Consider if this should return Optional or throw
    List<Application> findApplicationsByStatus(String status);
    Application updateApplicationStatus(Long id, String status);
    void deleteApplication(Long id);
}
