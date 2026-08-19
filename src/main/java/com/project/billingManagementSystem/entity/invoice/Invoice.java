package com.project.billingManagementSystem.entity.invoice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private String invoiceNo;
    private LocalDate date;
    private BigDecimal totalAmount;
    private PaymentStatus status;
    private BigDecimal advanceAmount;
    private BigDecimal balanceAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    @JsonBackReference
    private Customer customer;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Items> items;



}
