package com.example.jobmanagementsystem.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    void init();
    String storeFile(MultipartFile file, String subDirectory);
    Resource loadFileAsResource(String filePath); // filePath is relative to the root upload dir
    void deleteFile(String filePath) throws Exception; // filePath is relative to the root upload dir
    Path getFileStorageLocation();
}
