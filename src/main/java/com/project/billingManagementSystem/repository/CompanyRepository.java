package com.project.billingManagementSystem.repository;

import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
