package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.repository.CustomerRepository;
import com.project.billingManagementSystem.repository.InvoiceRepository;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    CustomerRepository customerRepository;


    @Override
    public Invoice createInvoice(Invoice invoice) {

        String customerPhone = invoice.getCustomer().getPhoneNo();

        Customer customer = customerRepository.findByPhoneNo(customerPhone)
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + customerPhone));

        invoice.setCustomer(customer);

        return repository.save(invoice);
    }

    @Override
    public List<Invoice> listInvoice() {
        return repository.findAll();
    }

    @Override
    public Invoice findInvoiceNO(String invoiceNo) {

        if(invoiceNo != null){

            Invoice invoice = repository.findByInvoiceNo(invoiceNo);
            return invoice;

        }
        return null;
    }


    @Override
    public Invoice updatingDataAndAmount(String invoiceNo, Invoice invoice) {
        if (invoice != null) {

            Invoice listInvoice = repository.findByInvoiceNo(invoiceNo);
            if (listInvoice != null) {
                if (invoice.getStatus() != null) {
                    listInvoice.setStatus(invoice.getStatus());
                }
                if (invoice.getDate() != null) {
                    listInvoice.setDate(invoice.getDate());
                }
                if (invoice.getTotalAmount() != null) {
                    listInvoice.setTotalAmount(invoice.getTotalAmount());
                }
                return repository.save(listInvoice);
            }
        }
        return null;
    }


}
