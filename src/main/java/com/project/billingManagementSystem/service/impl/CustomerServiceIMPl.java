package com.project.billingManagementSystem.service.impl;

import java.util.List;
import java.util.Optional;

import com.project.billingManagementSystem.enums.CustomerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.repository.CustomerRepository;
import com.project.billingManagementSystem.service.CustomerService;

@Component
public class CustomerServiceIMPl implements CustomerService {

	@Autowired
	private CustomerRepository repository;


	@Override
	public boolean addCustomer(Customer customer) {

		Optional<Customer> existingCustomer =
				repository.findByPhoneNo(customer.getPhoneNo());

		if (existingCustomer.isPresent()) {
			return false;
		}

		customer.setStatus(CustomerStatus.Active);
		repository.save(customer);

		return true;
	}
	

	@Override
	public List<Customer> getCustomersList() {
		return repository.findAll();
	}

	@Override
	public String findByPhoneNumber(String phoneNumber) {
		Optional<Customer> customer = repository.findByPhoneNo(phoneNumber);
		if (customer.isPresent()) {

			return String.format("Customer is already present with this Phone No.: %s in Database", customer.get().getPhoneNo());
		}
		return String.format("This phone No.: %s is Not present in database", phoneNumber);
	}

	@Override
	public Customer findByCustomerNumber(String phoneNo) {

		Optional<Customer> customer = repository.findByPhoneNo(phoneNo);
		if (customer.isPresent() &&
				customer.get().getStatus() != CustomerStatus.Deleted) {
			return customer.get();
		}


		return null;
	}

	@Override
	public Customer updateCustomerDetails(String phoneNo, Customer customer) {

		Optional<Customer> customerPhone = repository.findByPhoneNo(phoneNo);

		if (customerPhone.isPresent()) {

			Customer updateCustomer = customerPhone.get();
			if (updateCustomer.getStatus() == CustomerStatus.Deleted) {
				return updateCustomer;
			}

			if (customer.getCustomerName() != null) {
				updateCustomer.setCustomerName(customer.getCustomerName());
			}

			if (customer.getCustomerLastName() != null) {
				updateCustomer.setCustomerLastName(customer.getCustomerLastName());
			}

			if (customer.getEmailId() != null) {
				updateCustomer.setEmailId(customer.getEmailId());
			}

			if (customer.getPhoneNo() != null) {
				updateCustomer.setPhoneNo(customer.getPhoneNo());
			}

			return repository.save(updateCustomer);
		}

		return null;
	}

	@Override
	public void deleteCustomer(String phoneNo) {

		Optional<Customer> customer = repository.findByPhoneNo(phoneNo);

		if (customer.isPresent()) {

			Customer existingCustomer = customer.get();
			existingCustomer.setStatus(CustomerStatus.Deleted);

			repository.save(existingCustomer);

			System.out.println("Customer deleted successfully.");
		} else {
			System.out.println("Customer not found.");
		}
	}

}
