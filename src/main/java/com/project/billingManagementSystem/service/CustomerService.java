package com.project.billingManagementSystem.service;


import java.util.List;

import com.project.billingManagementSystem.entity.Customer;

public interface CustomerService {
	
	boolean addCustomer(Customer customer);
	
	List<Customer> getCustomersList();

	String findByPhoneNumber(String phoneNumber);

	Customer findByCustomerNumber(String phoneNo);

	Customer updateCustomerDetails(String phoneNo, Customer customer);

	void deleteCustomer(String phoneNo);


	
	
	

}
