package com.project.billingManagementSystem.service;

import com.project.billingManagementSystem.entity.invoice.Invoice;

import java.util.List;

public interface InvoiceService {

    Invoice createInvoice(Invoice invoice);

    List<Invoice> listInvoice();

    Invoice findInvoiceNO(String invoiceNo);

    Invoice updatingDataAndAmount(String invoiceNo,Invoice invoice);



}
