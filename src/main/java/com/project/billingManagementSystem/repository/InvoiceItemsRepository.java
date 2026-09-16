package com.project.billingManagementSystem.repository;

import com.project.billingManagementSystem.entity.invoice.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<Items, Long> {

    /*
     * One invoice can have multiple items.
     */
    List<Items> findByInvoice_InvoiceId(Long invoiceId);


    /*
     * One invoice + one serial number identifies one item.
     */
    Optional<Items> findByInvoice_InvoiceIdAndItemSerialNo(
            Long invoiceId,
            Integer itemSerialNo
    );


    /*
     * Get the highest item serial number for an invoice.
     *
     * Example:
     * Items: 1, 2, 3, 4
     * MAX = 4
     *
     * Next item = 5
     */
    @Query("""
            SELECT MAX(i.itemSerialNo)
            FROM Items i
            WHERE i.invoice.invoiceId = :invoiceId
            """)
    Integer findMaxItemSerialNoByInvoiceId(
            @Param("invoiceId") Long invoiceId
    );
}

