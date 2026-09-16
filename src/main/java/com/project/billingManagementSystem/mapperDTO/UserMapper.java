package com.project.billingManagementSystem.mapperDTO;


import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.dto.updateDTO.CompanyUpdateRequest;
import com.project.billingManagementSystem.securityapp.entity.Users;
import com.project.billingManagementSystem.securityapp.entity.dto.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    Users toEntity(UserRequest request);

    void updateEntity(UserRequest request, @MappingTarget Users users);
}
