package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.exception.FileStorageException;
import com.example.jobmanagementsystem.exception.ResourceNotFoundException; // Can be MyFileNotFoundException
import com.example.jobmanagementsystem.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct // Ensures this method is called after dependency injection is done
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check for invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // Generate a unique file name to prevent collisions
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                // No extension or other error
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocationDir = this.fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetLocationDir); // Ensure sub-directory exists

            Path targetLocation = targetLocationDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the path relative to the base upload directory, including subdirectory
            return Paths.get(subDirectory, uniqueFileName).toString();

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String relativeFilePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativeFilePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + relativeFilePath);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + relativeFilePath, ex);
        }
    }

    @Override
    public void deleteFile(String relativeFilePath) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativeFilePath).normalize();
            if (Files.exists(filePath)) {
                 Files.delete(filePath);
            } else {
                // Optionally log or ignore if file not found for deletion
                // throw new ResourceNotFoundException("File not found, cannot delete " + relativeFilePath);
            }
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + relativeFilePath + ". Please try again!", ex);
        }
    }

    @Override
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
