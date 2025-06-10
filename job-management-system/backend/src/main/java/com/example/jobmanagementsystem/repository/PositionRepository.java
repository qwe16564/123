package com.example.jobmanagementsystem.repository;

import com.example.jobmanagementsystem.entity.Company;
import com.example.jobmanagementsystem.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByCompany(Company company);
    List<Position> findByTitleContainingIgnoreCase(String title);
    List<Position> findByLocationAndEmploymentType(String location, String employmentType);
    List<Position> findByStatus(String status);
    long countByStatus(String status); // For statistics
}
