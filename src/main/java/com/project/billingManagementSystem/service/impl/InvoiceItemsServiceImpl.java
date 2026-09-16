package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.dto.createDTO.ItemsRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.ItemsUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.repository.InvoiceItemsRepository;
import com.project.billingManagementSystem.service.InvoiceItemsService;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InvoiceItemsServiceImpl implements InvoiceItemsService {

    private final InvoiceItemsRepository repository;
    private final InvoiceService invoiceService;


    public InvoiceItemsServiceImpl(InvoiceItemsRepository repository, InvoiceService invoiceService) {
        this.repository = repository;
        this.invoiceService = invoiceService;
    }

    @Override
    public Items addItems(String invoiceNo, ItemsRequest request) {

        validateInvoiceNo(invoiceNo);

        if (request == null) {
            throw new IllegalArgumentException("Item data is required");
        }


        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo.trim());

        validateCreateRequest(request);


        Items item = new Items();

        item.setDescription(request.getDescription().trim());

        Integer quantity = request.getQuantity();

        if (quantity == null) {
            quantity = 1;
        }

        item.setQuantity(quantity);

        item.setRate(request.getRate());


        Integer lastSerialNo = repository.findMaxItemSerialNoByInvoiceId(invoice.getInvoiceId());

        int nextSerialNo = lastSerialNo == null ? 1 : lastSerialNo + 1;

        item.setItemSerialNo(nextSerialNo);
        BigDecimal amount = calculateAmount(quantity, request.getRate());

        item.setAmount(amount);


        item.setInvoice(invoice);


        Items savedItem = repository.save(item);

        invoiceService.recalculateInvoice(invoice);


        return savedItem;
    }


    @Override
    public List<Items> addItems(String invoiceNo, List<ItemsRequest> requests) {

        validateInvoiceNo(invoiceNo);

        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Item list cannot be empty");
        }

        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo.trim());
        Integer lastSerialNo = repository.findMaxItemSerialNoByInvoiceId(invoice.getInvoiceId());

        int nextSerialNo = lastSerialNo == null ? 1 : lastSerialNo + 1;


        List<Items> items = new ArrayList<>();


        for (ItemsRequest request : requests) {

            if (request == null) {
                throw new IllegalArgumentException("Item cannot be null");
            }
            validateCreateRequest(request);
            Items item = new Items();
            item.setDescription(request.getDescription().trim());
            Integer quantity = request.getQuantity();

            if (quantity == null) {
                quantity = 1;
            }

            item.setQuantity(quantity);

            item.setRate(request.getRate());

            item.setItemSerialNo(nextSerialNo++);

            BigDecimal amount = calculateAmount(quantity, request.getRate());

            item.setAmount(amount);

            item.setInvoice(invoice);

            items.add(item);
        }

        List<Items> savedItems = repository.saveAll(items);

        invoiceService.recalculateInvoice(invoice);

        return savedItems;
    }


    @Override
    public Items updateInvoiceItems(String invoiceNo, Integer serialNo, ItemsUpdateRequest request) {

        validateInvoiceNo(invoiceNo);

        if (serialNo == null || serialNo <= 0) {

            throw new IllegalArgumentException("Valid Item Serial No. is required");
        }

        if (request == null) {

            throw new IllegalArgumentException("Item update data is required");
        }

        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo.trim());

        Items existingItem = repository.findByInvoice_InvoiceIdAndItemSerialNo(invoice.getInvoiceId(), serialNo).orElseThrow(() -> new RuntimeException("Item not found with serialNo: " + serialNo + " for invoiceNo: " + invoiceNo));

        if (request.getDescription() == null || request.getDescription().isBlank()) {

            throw new IllegalArgumentException("Item description is required");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {

            throw new IllegalArgumentException("Quantity must be greater than 0");
        }


        if (request.getRate() == null || request.getRate().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Rate must be greater than zero");
        }

        existingItem.setDescription(request.getDescription().trim());

        existingItem.setQuantity(request.getQuantity());

        existingItem.setRate(request.getRate());

        BigDecimal newAmount = calculateAmount(existingItem.getQuantity(), existingItem.getRate());

        existingItem.setAmount(newAmount);
        Items savedItem = repository.save(existingItem);

        invoiceService.recalculateInvoice(invoice);


        return savedItem;
    }



    @Override
    public Items deleteInvoiceItem(String invoiceNo, Integer serialNo) {

        validateInvoiceNo(invoiceNo);

        if (serialNo == null || serialNo <= 0) {

            throw new IllegalArgumentException("Valid Item Serial No. is required");
        }


        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo.trim());


        Items existingItem = repository.findByInvoice_InvoiceIdAndItemSerialNo(invoice.getInvoiceId(), serialNo).orElseThrow(() -> new RuntimeException("Item not found with serialNo: " + serialNo + " for invoiceNo: " + invoiceNo));

        repository.delete(existingItem);

        repository.flush();

        invoiceService.recalculateInvoice(invoice);


        return existingItem;
    }


    private void validateCreateRequest(ItemsRequest request) {

        // Description
        if (request.getDescription() == null || request.getDescription().isBlank()) {

            throw new IllegalArgumentException("Item description is required");
        }


        // Rate
        if (request.getRate() == null) {

            throw new IllegalArgumentException("Rate is required");
        }


        if (request.getRate().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException("Rate must be greater than zero");
        }


        // Quantity
        if (request.getQuantity() != null && request.getQuantity() <= 0) {

            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }



    private BigDecimal calculateAmount(Integer quantity, BigDecimal rate) {

        if (quantity == null) {
            quantity = 1;
        }

        if (rate == null) {
            throw new IllegalArgumentException("Rate is required");
        }

        return rate.multiply(BigDecimal.valueOf(quantity));
    }


    private void validateInvoiceNo(String invoiceNo) {

        if (invoiceNo == null || invoiceNo.isBlank()) {

            throw new IllegalArgumentException("Invoice number is required");
        }
    }
}

