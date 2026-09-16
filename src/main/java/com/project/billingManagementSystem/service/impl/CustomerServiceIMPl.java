package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.CustomerRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CustomerUpdateRequest;
import com.project.billingManagementSystem.enums.CustomerStatus;
import com.project.billingManagementSystem.mapperDTO.CustomerMapper;
import com.project.billingManagementSystem.repository.CustomerRepository;
import com.project.billingManagementSystem.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceIMPl implements CustomerService {

    private final CustomerRepository repository;

    private  final CustomerMapper customerMapper;

    public CustomerServiceIMPl(CustomerRepository repository, CustomerMapper customerMapper) {
        this.repository = repository;
        this.customerMapper = customerMapper;
    }

    /**
     * Add Customer
     */
    @Override
    public Customer addCustomer(CustomerRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Customer request cannot be null");
        }

        Optional<Customer> existingCustomer = repository.findByPhoneNo(request.getPhoneNo());

        if (existingCustomer.isPresent() && existingCustomer.get().getStatus() != CustomerStatus.Deleted) {

            throw new IllegalStateException("Customer already exists with phone number: " + request.getPhoneNo());
        }

        if (existingCustomer.isPresent() && existingCustomer.get().getStatus() == CustomerStatus.Deleted) {

            throw new IllegalStateException("Customer already exists with this phone number: " + request.getPhoneNo()+" in Database");
        }

        Customer customer = customerMapper.toEntity(request);

        customer.setStatus(CustomerStatus.Active);

        return repository.save(customer);
    }

    /**
     * Get all customers
     */
    @Override
    public List<Customer> getCustomersList() {

        return repository.findAll();
    }

    /**
     * Check customer by phone number
     */
    @Override
    public String findByPhoneNumber(String phoneNumber) {

        Optional<Customer> customer = repository.findByPhoneNo(phoneNumber);

        if (customer.isPresent() && customer.get().getStatus() != CustomerStatus.Deleted) {

            return String.format("Customer is already present with this Phone No.: %s in Database", customer.get().getPhoneNo());
        }

        if (customer.isPresent() && customer.get().getStatus() != CustomerStatus.Active) {

            return String.format("Customer is already present with this Phone No.: %s in Database but Deleted", customer.get().getPhoneNo());
        }

        return String.format("This phone No.: %s is not present in database", phoneNumber);
    }

    /**
     * Find customer by phone number
     */
    @Override
    public Customer findByCustomerNumber(String phoneNo) {

        return repository.findByPhoneNo(phoneNo).filter(customer -> customer.getStatus() != CustomerStatus.Deleted).orElseThrow(() -> new IllegalStateException("Customer not found with phone number: " + phoneNo));
    }

    /**
     * Update customer details
     * <p>
     * Only non-null fields are updated.
     */
    @Override
    public Customer updateCustomerDetails(String phoneNo, CustomerUpdateRequest request) {

        if (phoneNo == null || phoneNo.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        if (request == null) {
            throw new IllegalArgumentException("Customer update request cannot be null");
        }

        Customer existingCustomer = repository.findByPhoneNo(phoneNo).orElseThrow(() -> new IllegalStateException("Customer not found with phone number: " + phoneNo));

        // Do not update deleted customers
        if (existingCustomer.getStatus() == CustomerStatus.Deleted) {
            throw new IllegalStateException("Deleted customer cannot be updated");
        }

        /*
         * Update only the fields supplied by the client.
         */

        if (request.getCustomerName() != null) {
            existingCustomer.setCustomerName(request.getCustomerName());
        }

        if(request.getCustomerLastName()!= null){
            existingCustomer.setCustomerLastName(request.getCustomerLastName());
        }

        if (request.getEmailId() != null) {
            existingCustomer.setEmailId(request.getEmailId());
        }

        /*
         * If phone number itself is being changed,
         * make sure another customer doesn't already use it.
         */
        if (request.getPhoneNo() != null && !request.getPhoneNo().equals(existingCustomer.getPhoneNo())) {

            Optional<Customer> customerWithNewPhone = repository.findByPhoneNo(request.getPhoneNo());

            if (customerWithNewPhone.isPresent() && customerWithNewPhone.get().getStatus() != CustomerStatus.Deleted) {

                throw new IllegalStateException("Another customer already exists with phone number: " + request.getPhoneNo());
            }

            existingCustomer.setPhoneNo(request.getPhoneNo());
        }

        return repository.save(existingCustomer);
    }

    /**
     * Soft delete customer
     */
    @Override
    public void deleteCustomer(String phoneNo) {

        Customer customer = repository.findByPhoneNo(phoneNo).orElseThrow(() -> new IllegalStateException("Customer not found with phone number: " + phoneNo));

        if (customer.getStatus() == CustomerStatus.Deleted) {
            throw new IllegalStateException("Customer is already deleted");
        }

        customer.setStatus(CustomerStatus.Deleted);

        repository.save(customer);
    }
}

