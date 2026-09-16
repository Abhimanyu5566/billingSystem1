package com.project.billingManagementSystem.mapperDTO;


import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.CompanyRequest;
import com.project.billingManagementSystem.entity.dto.createDTO.CustomerRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CustomerUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    Customer toEntity(CustomerRequest request);


    void updateEntity(CustomerRequest request, @MappingTarget Customer customer);



    Customer toEntity(CustomerUpdateRequest request);

    void updateEntity(CustomerUpdateRequest request, @MappingTarget Customer customer);



}
