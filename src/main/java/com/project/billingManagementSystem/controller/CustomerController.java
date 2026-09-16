package com.project.billingManagementSystem.controller;

import java.util.List;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.CustomerRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.CustomerUpdateRequest;
import com.project.billingManagementSystem.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Add Customer
     */
    @PostMapping("/add-customer")
    public ResponseEntity<APIResponse<Customer>> addCustomer(@Valid @RequestBody CustomerRequest request) {

        Customer savedCustomer = customerService.addCustomer(request);
        APIResponse<Customer> response = APIResponse.<Customer>builder().success(true).code(HttpStatus.CREATED.value()).message("Customer added successfully.").data(savedCustomer).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all customers
     */
    @GetMapping("/list-customer")
    public ResponseEntity<APIResponse<List<Customer>>> getListCustomer() {

        List<Customer> customers = customerService.getCustomersList();
        APIResponse<List<Customer>> response = APIResponse.<List<Customer>>builder().success(true).code(HttpStatus.OK.value()).message("Customer list fetched successfully.").data(customers).build();
        return ResponseEntity.ok(response);
    }

    /**
     * Check customer by phone number
     * <p>
     * Example:
     * GET /customer/check-phoneNo?phoneNo=9876543210
     */
    @GetMapping("/check-phoneNo")
    public ResponseEntity<APIResponse<String>> checkPhoneCustomer(@RequestParam String phoneNo) {
        String message = customerService.findByPhoneNumber(phoneNo);
        APIResponse<String> response = APIResponse.<String>builder().success(true).code(HttpStatus.OK.value()).message("Customer phone check completed.").data(message).build();
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer by phone number
     * <p>
     * Example:
     * GET /customer/get-customer?phoneNo=9876543210
     */
    @GetMapping("/get-customer")
    public ResponseEntity<APIResponse<Customer>> getCustomer(@RequestParam String phoneNo) {
        Customer customer = customerService.findByCustomerNumber(phoneNo);
        APIResponse<Customer> response = APIResponse.<Customer>builder().success(true).code(HttpStatus.OK.value()).message("Customer details fetched successfully.").data(customer).build();
        return ResponseEntity.ok(response);
    }

    /**
     * Update customer details
     * <p>
     * Example:
     * PUT /customer/update/customer-details/phone=9876543210
     */
    @PutMapping("/update/customer-details/phone={phoneNo}")
    public ResponseEntity<APIResponse<Customer>> updateCustomerDetails(@PathVariable String phoneNo, @Valid @RequestBody CustomerUpdateRequest request) {

        Customer updatedCustomer = customerService.updateCustomerDetails(phoneNo, request);
        APIResponse<Customer> response = APIResponse.<Customer>builder().success(true).code(HttpStatus.OK.value()).message("Customer details updated successfully.").data(updatedCustomer).build();
        return ResponseEntity.ok(response);
    }

    /**
     * Delete customer
     * <p>
     * This performs a soft delete by changing
     * status to Deleted.
     */
    @DeleteMapping("/delete-customer")
    public ResponseEntity<APIResponse<String>> deleteCustomer(@RequestParam String phoneNo) {
        customerService.deleteCustomer(phoneNo);
        APIResponse<String> response = APIResponse.<String>builder().success(true).code(HttpStatus.OK.value()).message("Customer deleted successfully.").data(phoneNo).build();
        return ResponseEntity.ok(response);
    }
}
