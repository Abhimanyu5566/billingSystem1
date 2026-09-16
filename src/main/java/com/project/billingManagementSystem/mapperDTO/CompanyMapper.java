package com.project.billingManagementSystem.mapperDTO;

import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.dto.createDTO.CompanyRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    Company toEntity(CompanyRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CompanyRequest request, @MappingTarget Company company);


    @Mapping(target = "id", ignore = true)
    Company toEntity(CompanyUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CompanyUpdateRequest request, @MappingTarget Company company);
}