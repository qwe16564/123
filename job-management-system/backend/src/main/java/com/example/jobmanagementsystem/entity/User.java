package com.example.jobmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Relationships

    // For ON DELETE SET NULL, JPA side usually doesn't cascade remove.
    // The database constraint handles setting FK to null.
    // If we want to remove them when User is removed, then CascadeType.REMOVE/ALL is appropriate.
    // But SQL ON DELETE SET NULL implies they should not be removed.
    @OneToMany(mappedBy = "createdByUser") // Removed: cascade = CascadeType.ALL, orphanRemoval = true
    private List<Company> createdCompanies;

    @OneToMany(mappedBy = "postedByUser") // Removed: cascade = CascadeType.ALL, orphanRemoval = true
    private List<Position> postedPositions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes;

    @OneToMany(mappedBy = "applicantUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;
}
