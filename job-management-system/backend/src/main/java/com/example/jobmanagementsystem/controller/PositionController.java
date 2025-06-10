package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.CreatePositionRequest;
import com.example.jobmanagementsystem.dto.PositionDto;
import com.example.jobmanagementsystem.dto.UpdatePositionRequest;
import com.example.jobmanagementsystem.dto.UpdatePositionStatusRequest;
import com.example.jobmanagementsystem.entity.Position;
import com.example.jobmanagementsystem.service.PositionService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/positions")
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    private PositionDto convertToDto(Position position) {
        PositionDto dto = new PositionDto();
        dto.setId(position.getId());
        if (position.getCompany() != null) {
            dto.setCompanyId(position.getCompany().getId());
            dto.setCompanyName(position.getCompany().getName());
        }
        dto.setTitle(position.getTitle());
        dto.setDescription(position.getDescription());
        dto.setRequirements(position.getRequirements());
        dto.setLocation(position.getLocation());
        dto.setEmploymentType(position.getEmploymentType());
        dto.setStatus(position.getStatus());
        if (position.getPostedByUser() != null) {
            dto.setPostedByUserId(position.getPostedByUser().getId());
            dto.setPostedByUsername(position.getPostedByUser().getUsername());
        }
        dto.setCreatedAt(position.getCreatedAt());
        dto.setUpdatedAt(position.getUpdatedAt());
        return dto;
    }

    private Position convertToEntity(CreatePositionRequest dto) {
        Position position = new Position();
        // companyId and postedByUserId from DTO will be used by service
        position.setTitle(dto.getTitle());
        position.setDescription(dto.getDescription());
        position.setRequirements(dto.getRequirements());
        position.setLocation(dto.getLocation());
        position.setEmploymentType(dto.getEmploymentType());
        // Status is usually defaulted by service or DB
        return position;
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(@Valid @RequestBody CreatePositionRequest createPositionRequest) {
        Position positionToCreate = convertToEntity(createPositionRequest);
        Position createdPosition = positionService.createPosition(
                positionToCreate,
                createPositionRequest.getCompanyId(),
                createPositionRequest.getPostedByUserId()
        );
        return new ResponseEntity<>(convertToDto(createdPosition), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionById(id);
        return ResponseEntity.ok(convertToDto(position));
    }

    @GetMapping
    public ResponseEntity<List<PositionDto>> getAllPositions(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String status) {
        List<Position> positions;
        if (companyId != null) {
            positions = positionService.getPositionsByCompanyId(companyId);
        } else if (title != null) {
            positions = positionService.searchPositionsByTitle(title);
        } else if (location != null && employmentType != null) {
            positions = positionService.findPositionsByLocationAndEmploymentType(location, employmentType);
        } else if (status != null) {
            positions = positionService.findPositionsByStatus(status);
        }
         else {
            positions = positionService.getAllPositions();
        }
        List<PositionDto> positionDtos = positions.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(positionDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionDto> updatePosition(@PathVariable Long id, @Valid @RequestBody UpdatePositionRequest updatePositionRequest) {
        Position positionDetails = new Position(); // Temp entity for updates
        positionDetails.setTitle(updatePositionRequest.getTitle());
        positionDetails.setDescription(updatePositionRequest.getDescription());
        positionDetails.setRequirements(updatePositionRequest.getRequirements());
        positionDetails.setLocation(updatePositionRequest.getLocation());
        positionDetails.setEmploymentType(updatePositionRequest.getEmploymentType());
        positionDetails.setStatus(updatePositionRequest.getStatus());

        Position updatedPosition = positionService.updatePosition(id, positionDetails);
        return ResponseEntity.ok(convertToDto(updatedPosition));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PositionDto> updatePositionStatus(@PathVariable Long id, @Valid @RequestBody UpdatePositionStatusRequest statusRequest) {
        Position updatedPosition = positionService.updatePositionStatus(id, statusRequest.getStatus());
        return ResponseEntity.ok(convertToDto(updatedPosition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}
