package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.entity.Company;
import com.example.jobmanagementsystem.entity.Position;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.exception.ResourceNotFoundException;
import com.example.jobmanagementsystem.repository.CompanyRepository;
import com.example.jobmanagementsystem.repository.PositionRepository;
import com.example.jobmanagementsystem.repository.UserRepository;
import com.example.jobmanagementsystem.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Position createPosition(Position position, Long companyId, Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        position.setCompany(company);
        position.setPostedByUser(user);
        return positionRepository.save(position);
    }

    @Override
    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));
    }

    @Override
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    @Override
    public List<Position> getPositionsByCompanyId(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        return positionRepository.findByCompany(company);
    }

    @Override
    public List<Position> searchPositionsByTitle(String title) {
        return positionRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Position> findPositionsByLocationAndEmploymentType(String location, String employmentType) {
        return positionRepository.findByLocationAndEmploymentType(location, employmentType);
    }

    @Override
    public List<Position> findPositionsByStatus(String status) {
        return positionRepository.findByStatus(status);
    }

    @Override
    public Position updatePosition(Long id, Position positionDetails) {
        Position position = getPositionById(id);
        position.setTitle(positionDetails.getTitle());
        position.setDescription(positionDetails.getDescription());
        position.setRequirements(positionDetails.getRequirements());
        position.setLocation(positionDetails.getLocation());
        position.setEmploymentType(positionDetails.getEmploymentType());
        position.setStatus(positionDetails.getStatus());
        // company and postedByUser are typically not changed after creation, or with specific logic
        return positionRepository.save(position);
    }

    @Override
    public Position updatePositionStatus(Long id, String status) {
        Position position = getPositionById(id);
        position.setStatus(status);
        return positionRepository.save(position);
    }

    @Override
    public void deletePosition(Long id) {
        Position position = getPositionById(id); // Ensures position exists
        positionRepository.delete(position);
    }
}
