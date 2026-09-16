package com.project.billingManagementSystem.entity.dto.createDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemsRequest {

    @NotBlank(message = "Item description is required")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Rate is required")
    @DecimalMin(
            value = "0.01",
            message = "Rate must be greater than zero"
    )
    private BigDecimal rate;
}

