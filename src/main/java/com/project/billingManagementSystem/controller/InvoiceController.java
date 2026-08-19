package com.project.billingManagementSystem.controller;


import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @PostMapping("/create-invoice")
    public ResponseEntity<APIResponse<Invoice>> createInvoice(@RequestBody Invoice invoice) {

        Invoice savedInvoice = service.createInvoice(invoice);

        APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                .data(savedInvoice)
                .message("Invoice created successfully")
                .code(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoice-list")
    public ResponseEntity<APIResponse<List<Invoice>>> listOfAllInvoice() {

        List<Invoice> lists = service.listInvoice();
        if(lists.isEmpty()){
            APIResponse<List<Invoice>> response = APIResponse.<List<Invoice>>builder()
                    .success(false)
                    .data(lists)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("No Invoice are present")
                    .build();

            return ResponseEntity.ok(response);

        }
        APIResponse<List<Invoice>> response = APIResponse.<List<Invoice>>builder()
                .success(true)
                .data(lists)
                .code(HttpStatus.OK.value())
                .message("Invoices fetched successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-invoice")
    public ResponseEntity<APIResponse<Invoice>> findInvoiceByInvoiceNo(@RequestBody Invoice invoice) {

        if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().trim().isEmpty()) {

            APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                    .success(false)
                    .data(null)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Invoice number is required")
                    .build();

            return ResponseEntity.badRequest().body(response);
        }

        Invoice checkedInvoice = service.findInvoiceNO(invoice.getInvoiceNo());

        if (checkedInvoice == null) {

            APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                    .success(false)
                    .data(null)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Invoice not found")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                .success(true)
                .data(checkedInvoice)
                .code(HttpStatus.OK.value())
                .message("Successfully Fetched Invoice")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-invoice/{invoiceNo}")
    public ResponseEntity<APIResponse<Invoice>> updateInvoice(
            @PathVariable("invoiceNo") String invoiceNo,
            @RequestBody Invoice invoice) {

        Invoice checkedInvoice = service.findInvoiceNO(invoiceNo);

        if (checkedInvoice != null) {

            Invoice updatedInvoice = service.updatingDataAndAmount(invoiceNo, invoice);

            APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                    .success(true)
                    .code(HttpStatus.OK.value())
                    .message("Invoice updated successfully")
                    .data(updatedInvoice)
                    .build();

            return ResponseEntity.ok(response);
        }

        APIResponse<Invoice> response = APIResponse.<Invoice>builder()
                .success(false)
                .code(HttpStatus.NOT_FOUND.value())
                .message("Invoice not found with invoice number: " + invoiceNo)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


}
