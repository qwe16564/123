package com.example.jobmanagementsystem.service;

import com.example.jobmanagementsystem.entity.Company;
import java.util.List;

public interface CompanyService {
    Company createCompany(Company company, Long userId);
    Company getCompanyById(Long id);
    List<Company> getAllCompanies();
    List<Company> searchCompaniesByName(String name);
    List<Company> findCompaniesByIndustry(String industry);
    Company updateCompany(Long id, Company companyDetails);
    void deleteCompany(Long id);
}
