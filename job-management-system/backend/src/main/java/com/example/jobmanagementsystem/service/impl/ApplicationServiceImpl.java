package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.entity.Application;
import com.example.jobmanagementsystem.entity.Position;
import com.example.jobmanagementsystem.entity.Resume;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.exception.ResourceNotFoundException;
import com.example.jobmanagementsystem.repository.ApplicationRepository;
import com.example.jobmanagementsystem.repository.PositionRepository;
import com.example.jobmanagementsystem.repository.ResumeRepository;
import com.example.jobmanagementsystem.repository.UserRepository;
import com.example.jobmanagementsystem.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ResumeRepository resumeRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  ResumeRepository resumeRepository,
                                  PositionRepository positionRepository,
                                  UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.resumeRepository = resumeRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Application createApplication(Application application, Long resumeId, Long positionId, Long applicantUserId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));
        User applicant = userRepository.findById(applicantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicantUserId));

        // Ensure the resume belongs to the applicant
        if (!resume.getUser().getId().equals(applicant.getId())) {
            throw new IllegalArgumentException("Resume does not belong to the applicant.");
        }

        application.setResume(resume);
        application.setPosition(position);
        application.setApplicantUser(applicant);
        // application_date and updated_at are handled by timestamps
        return applicationRepository.save(application);
    }

    @Override
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }

    @Override
    public List<Application> getApplicationsByApplicantId(Long applicantUserId) {
        User applicant = userRepository.findById(applicantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicantUserId));
        return applicationRepository.findByApplicantUser(applicant);
    }

    @Override
    public List<Application> getApplicationsByPositionId(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));
        return applicationRepository.findByPosition(position);
    }

    @Override
    public Application getApplicationByPositionAndApplicant(Long positionId, Long applicantUserId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));
        User applicant = userRepository.findById(applicantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicantUserId));
        return applicationRepository.findByPositionAndApplicantUser(position, applicant)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found for this position and applicant"));
    }

    @Override
    public List<Application> findApplicationsByStatus(String status) {
        return applicationRepository.findByStatus(status);
    }

    @Override
    public Application updateApplicationStatus(Long id, String status) {
        Application application = getApplicationById(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    @Override
    public void deleteApplication(Long id) {
        Application application = getApplicationById(id); // Ensures application exists
        applicationRepository.delete(application);
    }
}
