package com.project.billingManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.billingManagementSystem.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByPhoneNo(String phoneNo);

	
	
	
	
	

}
