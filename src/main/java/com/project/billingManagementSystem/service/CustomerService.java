/*
package com.project.billingManagementSystem.service;


import java.util.List;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.CustomerRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CustomerUpdateRequest;

public interface CustomerService {
	
	boolean addCustomer(CustomerRequest customer);
	
	List<Customer> getCustomersList();

	String findByPhoneNumber(String phoneNumber);

	Customer findByCustomerNumber(String phoneNo);

	Customer updateCustomerDetails(String phoneNo, CustomerUpdateRequest customer);

	void deleteCustomer(String phoneNo);


	
	
	

}
*/

package com.project.billingManagementSystem.service;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.CustomerRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CustomerUpdateRequest;

import java.util.List;

public interface CustomerService {

	Customer addCustomer(CustomerRequest request);

	List<Customer> getCustomersList();

	String findByPhoneNumber(String phoneNumber);

	Customer findByCustomerNumber(String phoneNo);

	Customer updateCustomerDetails(
			String phoneNo,
			CustomerUpdateRequest request
	);

	void deleteCustomer(String phoneNo);
}

