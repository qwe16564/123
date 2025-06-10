package com.example.jobmanagementsystem.service;

import com.example.jobmanagementsystem.entity.Position;
import java.util.List;

public interface PositionService {
    Position createPosition(Position position, Long companyId, Long userId);
    Position getPositionById(Long id);
    List<Position> getAllPositions();
    List<Position> getPositionsByCompanyId(Long companyId);
    List<Position> searchPositionsByTitle(String title);
    List<Position> findPositionsByLocationAndEmploymentType(String location, String employmentType);
    List<Position> findPositionsByStatus(String status);
    Position updatePosition(Long id, Position positionDetails);
    Position updatePositionStatus(Long id, String status);
    void deletePosition(Long id);
}
