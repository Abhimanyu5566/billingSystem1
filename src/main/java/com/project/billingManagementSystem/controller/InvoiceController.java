package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.dto.createDTO.InvoiceRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.InvoiceUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.service.InvoiceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }


    // =========================================================
    // CREATE INVOICE
    // =========================================================

    @PostMapping("/create-invoice")
    public ResponseEntity<APIResponse<Invoice>> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        Invoice savedInvoice = service.createInvoice(request);

        APIResponse<Invoice> response =
                APIResponse.<Invoice>builder()
                        .success(true)
                        .data(savedInvoice)
                        .message("Invoice created successfully")
                        .code(HttpStatus.CREATED.value())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ALL INVOICES
    // =========================================================

    @GetMapping("/invoice-list")
    public ResponseEntity<APIResponse<List<Invoice>>> listOfAllInvoice() {

        List<Invoice> invoices = service.listInvoice();

        if (invoices.isEmpty()) {

            APIResponse<List<Invoice>> response =
                    APIResponse.<List<Invoice>>builder()
                            .success(false)
                            .data(invoices)
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("No invoices are present")
                            .build();

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }


        APIResponse<List<Invoice>> response =
                APIResponse.<List<Invoice>>builder()
                        .success(true)
                        .data(invoices)
                        .code(HttpStatus.OK.value())
                        .message("Invoices fetched successfully")
                        .build();

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // FIND INVOICE BY INVOICE NUMBER
    // =========================================================

    @GetMapping("/find-invoice")
    public ResponseEntity<APIResponse<Invoice>> findInvoiceByInvoiceNo(
            @RequestParam String invoiceNo) {

        Invoice invoice = service.findInvoiceNO(invoiceNo);

        APIResponse<Invoice> response =
                APIResponse.<Invoice>builder()
                        .success(true)
                        .data(invoice)
                        .code(HttpStatus.OK.value())
                        .message("Invoice fetched successfully")
                        .build();

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // UPDATE INVOICE
    // =========================================================

    @PutMapping("/update-invoice/{invoiceNo}")
    public ResponseEntity<APIResponse<Invoice>> updateInvoice(
            @PathVariable String invoiceNo,
            @Valid @RequestBody InvoiceUpdateRequest request) {

        Invoice updatedInvoice =
                service.updatingDataAndAmount(
                        invoiceNo,
                        request
                );

        APIResponse<Invoice> response =
                APIResponse.<Invoice>builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Invoice updated successfully")
                        .data(updatedInvoice)
                        .build();

        return ResponseEntity.ok(response);
    }
}

