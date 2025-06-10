package com.example.jobmanagementsystem.service;

import com.example.jobmanagementsystem.entity.Resume;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResumeService {
    Resume createResume(MultipartFile resumeFile, String originalFileName, Long userId /*, other metadata if any */);
    Resume getResumeById(Long id);
    List<Resume> getResumesByUserId(Long userId);
    void deleteResume(Long id) throws Exception; // Can throw exception if file deletion fails
    // Note: Actual file upload logic will be handled at the controller/service layer,
    // this interface assumes file metadata (like path) is managed.
}
