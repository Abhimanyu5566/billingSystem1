package com.project.billingManagementSystem.entity.dto.createDTO;


import com.project.billingManagementSystem.enums.CustomerStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Customer first name is required")
    @Size(max = 50, message = "Customer first name must not exceed 50 characters")
    private String customerName;

    @Size(max = 50, message = "Customer first name must not exceed 50 characters")
    private String customerLastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String emailId;

//    @NotNull(message = "Customer status is required")
//    private CustomerStatus status;


}
