package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.CompanyDto;
import com.example.jobmanagementsystem.dto.CreateCompanyRequest;
import com.example.jobmanagementsystem.dto.UpdateCompanyRequest;
import com.example.jobmanagementsystem.entity.Company;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Manual DTO to Entity mapping
    private Company convertToEntity(CreateCompanyRequest dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setIndustry(dto.getIndustry());
        company.setWebsite(dto.getWebsite());
        company.setAddress(dto.getAddress());
        // createdByUserId will be used by the service to fetch and set the User entity
        return company;
    }

    // Manual Entity to DTO mapping
    private CompanyDto convertToDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setIndustry(company.getIndustry());
        dto.setWebsite(company.getWebsite());
        dto.setAddress(company.getAddress());
        if (company.getCreatedByUser() != null) {
            dto.setCreatedByUserId(company.getCreatedByUser().getId());
            dto.setCreatedByUsername(company.getCreatedByUser().getUsername());
        }
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CreateCompanyRequest createCompanyRequest) {
        Company companyToCreate = convertToEntity(createCompanyRequest);
        Company createdCompany = companyService.createCompany(companyToCreate, createCompanyRequest.getCreatedByUserId());
        return new ResponseEntity<>(convertToDto(createdCompany), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(convertToDto(company));
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAllCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String industry) {
        List<Company> companies;
        if (name != null && !name.isEmpty()) {
            companies = companyService.searchCompaniesByName(name);
        } else if (industry != null && !industry.isEmpty()) {
            companies = companyService.findCompaniesByIndustry(industry);
        } else {
            companies = companyService.getAllCompanies();
        }
        List<CompanyDto> companyDtos = companies.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(companyDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest updateCompanyRequest) {
        Company companyDetails = new Company(); // Temporary entity to hold updates
        companyDetails.setName(updateCompanyRequest.getName());
        companyDetails.setDescription(updateCompanyRequest.getDescription());
        companyDetails.setIndustry(updateCompanyRequest.getIndustry());
        companyDetails.setWebsite(updateCompanyRequest.getWebsite());
        companyDetails.setAddress(updateCompanyRequest.getAddress());

        Company updatedCompany = companyService.updateCompany(id, companyDetails);
        return ResponseEntity.ok(convertToDto(updatedCompany));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
