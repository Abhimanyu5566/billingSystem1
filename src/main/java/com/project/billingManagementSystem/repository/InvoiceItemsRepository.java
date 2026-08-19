package com.project.billingManagementSystem.repository;

import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<Items, Long> {

    Optional<Items> findByInvoice(Invoice invoice);

    Optional<Items> findByInvoice_InvoiceIdAndItemSerialNo(
            Long invoiceId,
            Integer itemSerialNo
    );

    @Query("""
            SELECT MAX(i.itemSerialNo)
            FROM Items i
            WHERE i.invoice.invoiceId = :invoiceId
            """)
    Integer findMaxItemSerialNoByInvoiceId(
            @Param("invoiceId") Long invoiceId
    );



}
