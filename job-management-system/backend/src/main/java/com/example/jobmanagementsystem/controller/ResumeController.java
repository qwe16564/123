package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.CreateResumeRequest; // Will not be used directly for multipart
import com.example.jobmanagementsystem.dto.ResumeDto;
import com.example.jobmanagementsystem.entity.Resume;
import com.example.jobmanagementsystem.entity.User; // For getting authenticated user
import com.example.jobmanagementsystem.service.FileStorageService;
import com.example.jobmanagementsystem.service.ResumeService;
import com.example.jobmanagementsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Autowired
    public ResumeController(ResumeService resumeService, UserService userService, FileStorageService fileStorageService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    private ResumeDto convertToDto(Resume resume) {
        ResumeDto dto = new ResumeDto();
        dto.setId(resume.getId());
        if (resume.getUser() != null) {
            dto.setUserId(resume.getUser().getId());
            dto.setUsername(resume.getUser().getUsername());
        }
        dto.setFilePath(resume.getFilePath()); // This is the stored path, not for direct client use usually
        dto.setOriginalFileName(resume.getOriginalFileName());
        dto.setContentText(resume.getContentText());
        dto.setUploadedAt(resume.getUploadedAt());
        dto.setUpdatedAt(resume.getUpdatedAt());
        return dto;
    }

    // No direct convertToEntity from DTO for multipart creation

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDto> createResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "originalFileName", required = false) String originalFileName,
            // UserId should be derived from authenticated principal on the backend
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        if (file.isEmpty()) {
            // Consider a custom exception or specific error DTO
            return ResponseEntity.badRequest().build();
        }

        Resume createdResume = resumeService.createResume(file, originalFileName, currentUser.getId());
        return new ResponseEntity<>(convertToDto(createdResume), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id, HttpServletRequest request) {
        Resume resume = resumeService.getResumeById(id); // Fetch resume details
        Resource resource = fileStorageService.loadFileAsResource(resume.getFilePath());

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.warn("Could not determine file type for resume id " + id);
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getOriginalFileName() + "\"")
                .body(resource);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResumeDto> getResumeById(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        return ResponseEntity.ok(convertToDto(resume));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeDto>> getResumesByUserId(@PathVariable Long userId, Authentication authentication) {
        // Security check: Ensure the authenticated user is requesting their own resumes or is an admin
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        if (!currentUser.getId().equals(userId) /* && !currentUser.getRole().equals("ADMIN") */) {
             return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Resume> resumes = resumeService.getResumesByUserId(userId);
        List<ResumeDto> resumeDtos = resumes.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(resumeDtos);
    }

    // Endpoint to get resumes for the currently authenticated user
    @GetMapping("/my-resumes")
    public ResponseEntity<List<ResumeDto>> getMyResumes(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        List<Resume> resumes = resumeService.getResumesByUserId(currentUser.getId());
        List<ResumeDto> resumeDtos = resumes.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(resumeDtos);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id, Authentication authentication) {
        Resume resume = resumeService.getResumeById(id);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        // Security check: User can only delete their own resume (or admin)
        if (!resume.getUser().getId().equals(currentUser.getId()) /* && !currentUser.getRole().equals("ADMIN") */ ) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            resumeService.deleteResume(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
