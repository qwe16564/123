package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.ApplicationDto;
import com.example.jobmanagementsystem.dto.CreateApplicationRequest;
import com.example.jobmanagementsystem.dto.UpdateApplicationStatusRequest;
import com.example.jobmanagementsystem.entity.Application;
import com.example.jobmanagementsystem.service.ApplicationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    private ApplicationDto convertToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        if (app.getResume() != null) {
            dto.setResumeId(app.getResume().getId());
            dto.setResumeFileName(app.getResume().getOriginalFileName());
        }
        if (app.getPosition() != null) {
            dto.setPositionId(app.getPosition().getId());
            dto.setPositionTitle(app.getPosition().getTitle());
        }
        if (app.getApplicantUser() != null) {
            dto.setApplicantUserId(app.getApplicantUser().getId());
            dto.setApplicantUsername(app.getApplicantUser().getUsername());
        }
        dto.setApplicationDate(app.getApplicationDate());
        dto.setStatus(app.getStatus());
        dto.setNotes(app.getNotes());
        dto.setUpdatedAt(app.getUpdatedAt());
        return dto;
    }

    private Application convertToEntity(CreateApplicationRequest dto) {
        Application app = new Application();
        // resumeId, positionId, applicantUserId used by service
        app.setStatus(dto.getStatus());
        app.setNotes(dto.getNotes());
        return app;
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> createApplication(@Valid @RequestBody CreateApplicationRequest createApplicationRequest) {
        Application applicationToCreate = convertToEntity(createApplicationRequest);
        Application createdApplication = applicationService.createApplication(
                applicationToCreate,
                createApplicationRequest.getResumeId(),
                createApplicationRequest.getPositionId(),
                createApplicationRequest.getApplicantUserId()
        );
        // Ensure sub-entities are loaded for DTO conversion if necessary
        Application fullApplication = applicationService.getApplicationById(createdApplication.getId());
        return new ResponseEntity<>(convertToDto(fullApplication), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(convertToDto(application));
    }

    @GetMapping("/applicant/{applicantUserId}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByApplicantId(@PathVariable Long applicantUserId) {
        List<Application> applications = applicationService.getApplicationsByApplicantId(applicantUserId);
        List<ApplicationDto> dtos = applications.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/position/{positionId}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByPositionId(@PathVariable Long positionId) {
        List<Application> applications = applicationService.getApplicationsByPositionId(positionId);
        List<ApplicationDto> dtos = applications.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/position/{positionId}/applicant/{applicantUserId}")
    public ResponseEntity<ApplicationDto> getApplicationByPositionAndApplicant(
            @PathVariable Long positionId, @PathVariable Long applicantUserId) {
        Application application = applicationService.getApplicationByPositionAndApplicant(positionId, applicantUserId);
        return ResponseEntity.ok(convertToDto(application));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDto>> findApplicationsByStatus(@RequestParam String status) {
        List<Application> applications = applicationService.findApplicationsByStatus(status);
        List<ApplicationDto> dtos = applications.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationDto>> getMyApplications(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Assuming User entity can be fetched via username from UserDetails
        // Or, if your UserDetails implementation (from UserServiceImpl) stores the ID, use that.
        // For now, let's assume userService.getUserByUsername exists and returns your User entity.
        com.example.jobmanagementsystem.entity.User currentUser = userService.getUserByUsername(userDetails.getUsername());

        List<Application> applications = applicationService.getApplicationsByApplicantId(currentUser.getId());
        List<ApplicationDto> dtos = applications.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationDto> updateApplicationStatus(@PathVariable Long id, @Valid @RequestBody UpdateApplicationStatusRequest statusRequest, Authentication authentication) {
        // Add authorization: Ensure only relevant users (e.g., recruiter for the position, admin) can update status
        // For now, basic auth is via SecurityConfig's .anyRequest().authenticated()
        Application application = applicationService.getApplicationById(id); // Fetch to check associations if needed for auth
        // TODO: Add role-based authorization logic here, e.g.:
        // UserDetails principal = (UserDetails) authentication.getPrincipal();
        // User currentUser = userService.getUserByUsername(principal.getUsername());
        // if (!canUpdateStatus(currentUser, application)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Application updatedApplication = applicationService.updateApplicationStatus(id, statusRequest.getStatus());
        return ResponseEntity.ok(convertToDto(updatedApplication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id, Authentication authentication) {
        // Add authorization: Ensure applicant can delete their own, or recruiter/admin
        Application application = applicationService.getApplicationById(id);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        com.example.jobmanagementsystem.entity.User currentUser = userService.getUserByUsername(principal.getUsername());

        if (!application.getApplicantUser().getId().equals(currentUser.getId()) /* && !isRecruiterOrAdmin() */) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method for authorization (example)
    // private boolean canUpdateStatus(User currentUser, Application application) {
    //    // Logic: if currentUser is admin, or if currentUser is the recruiter who posted the position
    //    if (currentUser.getRole().equals("ADMIN")) return true;
    //    if (currentUser.getRole().equals("RECRUITER") && application.getPosition().getPostedByUser().getId().equals(currentUser.getId())) return true;
    //    return false;
    // }
}
