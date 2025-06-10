package com.example.jobmanagementsystem.service.impl;

import com.example.jobmanagementsystem.entity.Company;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.exception.ResourceNotFoundException;
import com.example.jobmanagementsystem.repository.CompanyRepository;
import com.example.jobmanagementsystem.repository.UserRepository;
import com.example.jobmanagementsystem.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Company createCompany(Company company, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        company.setCreatedByUser(user);
        return companyRepository.save(company);
    }

    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public List<Company> searchCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Company> findCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustry(industry);
    }

    @Override
    public Company updateCompany(Long id, Company companyDetails) {
        Company company = getCompanyById(id);
        company.setName(companyDetails.getName());
        company.setDescription(companyDetails.getDescription());
        company.setIndustry(companyDetails.getIndustry());
        company.setWebsite(companyDetails.getWebsite());
        company.setAddress(companyDetails.getAddress());
        // createdByUser should typically not be changed after creation, or handled with specific logic
        // created_at and updated_at are handled by @CreationTimestamp and @UpdateTimestamp
        return companyRepository.save(company);
    }

    @Override
    public void deleteCompany(Long id) {
        Company company = getCompanyById(id); // Ensures company exists
        companyRepository.delete(company);
    }
}
