package com.example.jobmanagementsystem.repository;

import com.example.jobmanagementsystem.entity.Resume;
import com.example.jobmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUser(User user);
}
