package com.project.billingManagementSystem.service;

import com.project.billingManagementSystem.entity.dto.createDTO.InvoiceRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.InvoiceUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;

import java.util.List;

public interface InvoiceService {

    Invoice createInvoice(InvoiceRequest invoice);

    List<Invoice> listInvoice();

    Invoice findInvoiceNO(String invoiceNo);

    Invoice updatingDataAndAmount(String invoiceNo,InvoiceUpdateRequest invoice);

    void recalculateInvoice(Invoice invoice);



}
