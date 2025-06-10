package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.entity.Resume;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.exception.ResourceNotFoundException;
import com.example.jobmanagementsystem.repository.ResumeRepository;
import com.example.jobmanagementsystem.repository.UserRepository;
import com.example.jobmanagementsystem.service.FileStorageService;
import com.example.jobmanagementsystem.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private static final String RESUME_SUBDIRECTORY = "resumes";

    @Autowired
    public ResumeServiceImpl(ResumeRepository resumeRepository,
                             UserRepository userRepository,
                             FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Resume createResume(MultipartFile resumeFile, String clientOriginalFileName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String storedFilePath = fileStorageService.storeFile(resumeFile, RESUME_SUBDIRECTORY);

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFilePath(storedFilePath); // Store relative path
        // Use clientOriginalFileName if provided and valid, otherwise use the one from MultipartFile
        String originalFileNameToStore = (clientOriginalFileName != null && !clientOriginalFileName.isEmpty())
                                          ? clientOriginalFileName
                                          : resumeFile.getOriginalFilename();
        resume.setOriginalFileName(originalFileNameToStore);
        // resume.setContentText(); // TODO: Optional: Add OCR text extraction here or in an async job

        return resumeRepository.save(resume);
    }

    @Override
    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
    }

    @Override
    public List<Resume> getResumesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return resumeRepository.findByUser(user);
    }

    @Override
    public void deleteResume(Long id) throws Exception {
        Resume resume = getResumeById(id); // Ensures resume exists

        // Delete the actual file from storage
        if (resume.getFilePath() != null && !resume.getFilePath().isEmpty()) {
            fileStorageService.deleteFile(resume.getFilePath());
        }

        resumeRepository.delete(resume);
    }
}
