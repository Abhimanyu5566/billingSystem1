package com.project.billingManagementSystem.entity.invoice;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_invoice_item_serial", columnNames = {"invoiceId", "itemSerialNo"})})
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    private Integer itemSerialNo;
    private String description;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId")
    @JsonBackReference
    private Invoice invoice;


}
