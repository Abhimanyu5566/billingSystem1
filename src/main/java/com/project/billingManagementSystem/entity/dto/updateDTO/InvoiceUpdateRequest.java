package com.project.billingManagementSystem.entity.dto.updateDTO;


import com.project.billingManagementSystem.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceUpdateRequest {

    private LocalDate date;

    @DecimalMin(value = "0.00", message = "Advance amount cannot be negative")
    private BigDecimal advanceAmount;

    private PaymentStatus status;

}
