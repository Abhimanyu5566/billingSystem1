package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.dto.createDTO.InvoiceRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.InvoiceUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.enums.PaymentStatus;
import com.project.billingManagementSystem.mapperDTO.InvoiceMapper;
import com.project.billingManagementSystem.repository.CustomerRepository;
import com.project.billingManagementSystem.repository.InvoiceItemsRepository;
import com.project.billingManagementSystem.repository.InvoiceRepository;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repository;
    private final CustomerRepository customerRepository;
    private final InvoiceItemsRepository itemsRepository;
    private final InvoiceMapper invoiceMapper;


    public InvoiceServiceImpl(InvoiceRepository repository, CustomerRepository customerRepository, InvoiceItemsRepository itemsRepository, InvoiceMapper invoiceMapper) {

        this.repository = repository;
        this.customerRepository = customerRepository;
        this.itemsRepository = itemsRepository;
        this.invoiceMapper = invoiceMapper;
    }


    @Override
    public Invoice createInvoice(InvoiceRequest request) {

        if (request == null) {

            throw new IllegalArgumentException("Invoice request cannot be null");
        }


        if (request.getCustomerPhone() == null || request.getCustomerPhone().isBlank()) {

            throw new IllegalArgumentException("Customer phone number is required");
        }


        String phone = request.getCustomerPhone().trim();


        Customer customer = customerRepository.findByPhoneNo(phone).orElseThrow(() -> new RuntimeException("Customer not found with phone number: " + phone));



        Invoice invoice = invoiceMapper.toEntity(request);


        invoice.setInvoiceNo(generateInvoiceNumber());

        invoice.setDate(LocalDate.now());

        invoice.setCustomer(customer);

        invoice.setTotalAmount(BigDecimal.ZERO);

        invoice.setAdvanceAmount(BigDecimal.ZERO);

        invoice.setBalanceAmount(BigDecimal.ZERO);

        invoice.setStatus(PaymentStatus.PENDING);


        BigDecimal advance = request.getAdvanceAmount();

        if (advance != null && advance.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("Advance amount cannot be negative");
        }


        if (advance != null && advance.compareTo(BigDecimal.ZERO) > 0) {

            throw new IllegalArgumentException("Advance amount cannot be added before invoice items are added");
        }

        return repository.save(invoice);
    }


    private String generateInvoiceNumber() {

        long count = repository.count();

        long nextNumber = count + 1;

        return String.format("INV-%05d", nextNumber);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listInvoice() {

        return repository.findAll();
    }



    @Override
    @Transactional(readOnly = true)
    public Invoice findInvoiceNO(String invoiceNo) {

        if (invoiceNo == null || invoiceNo.isBlank()) {

            throw new IllegalArgumentException("Invoice number is required");
        }


        String trimmedInvoiceNo = invoiceNo.trim();


        Invoice invoice = repository.findByInvoiceNo(trimmedInvoiceNo);


        if (invoice == null) {

            throw new RuntimeException("Invoice not found with invoice number: " + trimmedInvoiceNo);
        }


        return invoice;
    }


    @Override
    public Invoice updatingDataAndAmount(String invoiceNo, InvoiceUpdateRequest request) {

        if (invoiceNo == null || invoiceNo.isBlank()) {

            throw new IllegalArgumentException("Invoice number is required");
        }


        if (request == null) {

            throw new IllegalArgumentException("Invoice update data cannot be null");
        }


        Invoice invoice = repository.findByInvoiceNo(invoiceNo.trim());


        if (invoice == null) {

            throw new RuntimeException("Invoice not found with invoice number: " + invoiceNo);
        }


        if (request.getAdvanceAmount() != null) {

            BigDecimal advance = request.getAdvanceAmount();


            if (advance.compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalArgumentException("Advance amount cannot be negative");
            }

            invoice.setAdvanceAmount(advance);

        }

        recalculateInvoice(invoice);


        return repository.save(invoice);
    }

    @Override
    public void recalculateInvoice(Invoice invoice) {

        if (invoice == null) {

            throw new IllegalArgumentException("Invoice cannot be null");
        }

        List<Items> items = itemsRepository.findByInvoice_InvoiceId(invoice.getInvoiceId());
        BigDecimal totalAmount = items.stream().map(Items::getAmount)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalAmount(totalAmount);
        BigDecimal advanceAmount = invoice.getAdvanceAmount();

        if (advanceAmount == null) {

            advanceAmount = BigDecimal.ZERO;
        }
        if (advanceAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("Advance amount cannot be negative");
        }
        if (advanceAmount.compareTo(totalAmount) > 0) {

            throw new IllegalArgumentException("Advance amount cannot be greater than total amount");
        }

        BigDecimal balanceAmount = totalAmount.subtract(advanceAmount);
        invoice.setBalanceAmount(balanceAmount);

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(PaymentStatus.PENDING);

        } else if (advanceAmount.compareTo(totalAmount) == 0) {
            invoice.setStatus(PaymentStatus.PAID);

        } else {
            invoice.setStatus(PaymentStatus.PENDING);
        }

        repository.save(invoice);
    }
}
