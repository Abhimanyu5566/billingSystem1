package com.project.billingManagementSystem.entity.dto.updateDTO;


import com.project.billingManagementSystem.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerUpdateRequest {

    @Size(max = 50, message = "Customer first name must not exceed 50 characters")
    private String customerName;

    @Size(max = 50, message = "Customer first name must not exceed 50 characters")
    private String customerLastName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNo;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String emailId;

    private CustomerStatus status;


}
