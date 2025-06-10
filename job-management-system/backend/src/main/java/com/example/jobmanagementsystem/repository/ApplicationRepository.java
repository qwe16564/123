package com.example.jobmanagementsystem.repository;

import com.example.jobmanagementsystem.entity.Application;
import com.example.jobmanagementsystem.entity.Position;
import com.example.jobmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantUser(User applicantUser);
    List<Application> findByPosition(Position position);
    List<Application> findByPositionAndApplicantUser(Position position, User applicantUser);
    List<Application> findByStatus(String status);
}
