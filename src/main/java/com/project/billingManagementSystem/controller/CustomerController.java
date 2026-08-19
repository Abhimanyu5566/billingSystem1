package com.project.billingManagementSystem.controller;

import java.util.List;

import com.project.billingManagementSystem.enums.CustomerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.service.CustomerService;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping("/add-customer")
    public ResponseEntity<APIResponse<String>> addCustomer(@RequestBody Customer customer) {

        if (service.addCustomer(customer)) {
            APIResponse<String> response = APIResponse.<String>builder().success(true).code(HttpStatus.CREATED.value()).message("Customer added successfully.").data(customer.getPhoneNo()).build();
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(response);
        }
        APIResponse<String> response = APIResponse.<String>builder().success(false).code(HttpStatus.CONFLICT.value()).message("Customer already exists.").data(null).build();

        return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping("/list-customer")
    public ResponseEntity<APIResponse<List<Customer>>> getListCustomer() {
        APIResponse<List<Customer>> response = APIResponse.<List<Customer>>builder().success(true).code(HttpStatus.OK.value()).data(service.getCustomersList()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-phoneNo")
    public ResponseEntity<APIResponse<String>> checkPhoneCustomer(@RequestBody Customer phoneNo) {
        String message = service.findByPhoneNumber(phoneNo.getPhoneNo());
        APIResponse<String> response = APIResponse.<String>builder().success(true).code(HttpStatus.OK.value()).data(message).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-customer")
    public ResponseEntity<APIResponse<Customer>> updateCustomerDetails(@RequestBody Customer customer) {
        Customer incustomer = service.findByCustomerNumber(customer.getPhoneNo());
        APIResponse<Customer> response = APIResponse.<Customer>builder().success(true).code(HttpStatus.OK.value()).data(incustomer).build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/customer-details/phone={phoneNo}")
    public ResponseEntity<APIResponse<Customer>> updateCustomerDetails(@PathVariable("phoneNo") String phoneNo, @RequestBody Customer customer) {
        Customer updateCustomer = service.updateCustomerDetails(phoneNo, customer);
        if (updateCustomer == null) {
            APIResponse<Customer> response = APIResponse.<Customer>builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Customer not Found").data(null).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (updateCustomer.getStatus() == CustomerStatus.Deleted) {
            APIResponse<Customer> response = APIResponse.<Customer>builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message("Customer is Deleted").data(updateCustomer).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        APIResponse<Customer> response = APIResponse.<Customer>builder().success(true).code(HttpStatus.OK.value()).data(updateCustomer).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-customer")
    public ResponseEntity<APIResponse<String>> deleteCustomer(@RequestBody Customer customer) {
        service.deleteCustomer(customer.getPhoneNo());
        APIResponse<String> response = APIResponse.<String>builder().success(true).code(HttpStatus.OK.value()).data(customer.getPhoneNo()).build();
        return ResponseEntity.ok(response);
    }
}
