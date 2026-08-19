package com.project.billingManagementSystem.repository;

import com.project.billingManagementSystem.entity.invoice.Invoice;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Invoice findByInvoiceNo(String invoiceNo);

}
