package com.project.billingManagementSystem.service;


import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.dto.createDTO.CompanyRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;

public interface CompanyService {


    Company createCompany(CompanyRequest company);

    Company getCompany();

    Company updateCompany(Long id, CompanyUpdateRequest company);

//    void deleteCompany(Long id);



}
