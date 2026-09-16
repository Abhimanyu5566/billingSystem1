package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.dto.createDTO.CompanyRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;
import com.project.billingManagementSystem.mapperDTO.CompanyMapper;
import com.project.billingManagementSystem.repository.CompanyRepository;
import com.project.billingManagementSystem.service.CompanyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CompanyServiceIMPL implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceIMPL(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /**
     * Create company.
     * Only one company is allowed in the system.
     */
    @Override
    public Company createCompany(CompanyRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Company request cannot be null");
        }

        // Single company system
        if (companyRepository.count() > 0) {
            throw new IllegalStateException("Company already exists");
        }

        Company company = companyMapper.toEntity(request);

        if (company == null) {
            throw new IllegalStateException("Unable to create company");
        }

        company.setCreateAt(LocalDateTime.now().toString());

        return companyRepository.save(company);
    }



    @Override
    public Company getCompany() {

        return companyRepository.findAll().stream().findFirst().orElseThrow(() ->
                new IllegalStateException("Company not configured"));
    }

    @Override
    public Company updateCompany(Long id, CompanyUpdateRequest request) {

        if (id == null) {
            throw new IllegalArgumentException("Company id cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Company update request cannot be null");
        }
        Company existingCompany = companyRepository.findById(id).orElseThrow(() -> new IllegalStateException("Company not found with id: " + id));
        Company updatedCompany = companyMapper.toEntity(request);

        if (updatedCompany == null) {
            throw new IllegalStateException("Unable to update company");
        }

        // Update only fields that are provided
        if (updatedCompany.getName() != null) {
            existingCompany.setName(updatedCompany.getName());
        }

        if (updatedCompany.getAddress() != null) {
            existingCompany.setAddress(updatedCompany.getAddress());
        }

        if (updatedCompany.getPhone() != null) {
            existingCompany.setPhone(updatedCompany.getPhone());
        }

        if (updatedCompany.getEmail() != null) {
            existingCompany.setEmail(updatedCompany.getEmail());
        }

        if (updatedCompany.getGstNumber() != null) {
            existingCompany.setGstNumber(updatedCompany.getGstNumber());
        }

        if (updatedCompany.getPanNumber() != null) {
            existingCompany.setPanNumber(updatedCompany.getPanNumber());
        }

        if (updatedCompany.getBankName() != null) {
            existingCompany.setBankName(updatedCompany.getBankName());
        }

        if (updatedCompany.getAccountHolderName() != null) {
            existingCompany.setAccountHolderName(updatedCompany.getAccountHolderName());
        }

        if (updatedCompany.getAccountNumber() != null) {
            existingCompany.setAccountNumber(updatedCompany.getAccountNumber());
        }

        if (updatedCompany.getIfscCode() != null) {
            existingCompany.setIfscCode(updatedCompany.getIfscCode());
        }

        if (updatedCompany.getUpiId() != null) {
            existingCompany.setUpiId(updatedCompany.getUpiId());
        }

        existingCompany.setUpdateAt(LocalDateTime.now().toString());

        return companyRepository.save(existingCompany);
    }
}

