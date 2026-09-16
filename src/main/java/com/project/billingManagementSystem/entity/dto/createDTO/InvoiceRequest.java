package com.project.billingManagementSystem.entity.dto.createDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceRequest {

    @NotBlank(message = "Customer phone number is required")
    private String customerPhone;

    @DecimalMin(value = "0.00", message = "Advance amount cannot be negative")
    private BigDecimal advanceAmount;


}

