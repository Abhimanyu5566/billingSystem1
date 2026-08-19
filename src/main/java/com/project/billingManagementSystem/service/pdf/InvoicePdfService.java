package com.project.billingManagementSystem.service.pdf;

public interface InvoicePdfService {

    byte[] generateInvoicePdf(String invoiceNo);
}
