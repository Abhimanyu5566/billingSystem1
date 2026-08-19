package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.InvoiceItemsService;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceItemsController {

    @Autowired
    private InvoiceItemsService service;

    @Autowired
    private InvoiceService invoiceService;


    @PostMapping("/add-invoice-item")
    public ResponseEntity<APIResponse<Items>> addInvoiceItems(@RequestBody Items items) {

        String invoiceNo = items.getInvoice().getInvoiceNo();
        System.out.println("Check Code: " + invoiceNo);

        if (invoiceNo == null || invoiceNo.isBlank()) {
            return ResponseEntity.badRequest().body(APIResponse.<Items>builder().code(HttpStatus.BAD_REQUEST.value()).message("Invoice No. is required").data(null).build());
        }

        if (invoiceService.findInvoiceNO(invoiceNo) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.<Items>builder().code(HttpStatus.NOT_FOUND.value()).message("Invoice No. Not Found").data(null).build());
        }

        Items addItem = service.addItems(invoiceNo, items);

        return ResponseEntity.ok(APIResponse.<Items>builder().code(HttpStatus.OK.value()).message("Successfully added items in invoice: " + invoiceNo).data(addItem).build());
    }

    @PutMapping("/update-invoiceItem/{invoiceNo}/{serialNo}")
    public ResponseEntity<APIResponse<Items>> updateInvoiceItem(
            @PathVariable String invoiceNo,
            @PathVariable Integer serialNo,
            @RequestBody Items updatedItem) {

        // Check invoice number
        if (invoiceNo == null || invoiceNo.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.<Items>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Invoice No. is required")
                            .data(null)
                            .success(false)
                            .build());
        }

        // Check serial number
        if (serialNo == null || serialNo <= 0) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.<Items>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Valid Item Serial No. is required")
                            .data(null)
                            .success(false)
                            .build());
        }

        // Check request body
        if (updatedItem == null) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.<Items>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Item data is required")
                            .data(null)
                            .success(false)
                            .build());
        }

        try {

            // Update item
            Items updated = service.updateInvoiceItems(
                    invoiceNo,
                    serialNo,
                    updatedItem
            );

            return ResponseEntity.ok(
                    APIResponse.<Items>builder()
                            .code(HttpStatus.OK.value())
                            .message(
                                    "Successfully updated item Serial No. "
                                            + serialNo
                                            + " in invoice: "
                                            + invoiceNo
                            )
                            .data(updated)
                            .success(true)
                            .build()
            );

        } catch (IllegalArgumentException e) {

            // Validation error
            return ResponseEntity.badRequest()
                    .body(APIResponse.<Items>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .success(false)
                            .build());

        } catch (RuntimeException e) {

            // Invoice or item not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.<Items>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .success(false)
                            .build());
        }
    }

    @PostMapping("/add-invoice-items")
    public ResponseEntity<APIResponse<List<Items>>> addInvoiceItems(
            @RequestParam String invoiceNo,
            @RequestBody List<Items> items) {

        if (invoiceNo == null || invoiceNo.isBlank()) {
            return ResponseEntity.badRequest().body(
                    APIResponse.<List<Items>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Invoice No. is required")
                            .data(null)
                            .build()
            );
        }

        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    APIResponse.<List<Items>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Item list cannot be empty")
                            .data(null)
                            .build()
            );
        }

        if (invoiceService.findInvoiceNO(invoiceNo) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    APIResponse.<List<Items>>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .message("Invoice No. Not Found: " + invoiceNo)
                            .data(null)
                            .build()
            );
        }

        try {

            List<Items> savedItems = service.addItems(
                    invoiceNo,
                    items
            );

            return ResponseEntity.ok(
                    APIResponse.<List<Items>>builder()
                            .code(HttpStatus.OK.value())
                            .message(
                                    "Successfully added "
                                            + savedItems.size()
                                            + " items to invoice: "
                                            + invoiceNo
                            )
                            .data(savedItems)
                            .build()
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    APIResponse.<List<Items>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    @DeleteMapping("/delete-invoiceItem/{invoiceNo}/{serialNo}")
    public ResponseEntity<APIResponse<Items>> deleteInvoiceItem(
            @PathVariable String invoiceNo,
            @PathVariable Integer serialNo) {

        // Check invoice number
        if (invoiceNo == null || invoiceNo.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Invoice No. is required")
                                    .data(null)
                                    .success(false)
                                    .build()
                    );
        }

        // Check serial number
        if (serialNo == null || serialNo <= 0) {
            return ResponseEntity.badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Valid Item Serial No. is required")
                                    .data(null)
                                    .success(false)
                                    .build()
                    );
        }

        try {

            // Delete item
            Items deletedItem = service.deleteInvoiceItem(
                    invoiceNo,
                    serialNo
            );

            return ResponseEntity.ok(
                    APIResponse.<Items>builder()
                            .code(HttpStatus.OK.value())
                            .message(
                                    "Successfully deleted item Serial No. "
                                            + serialNo
                                            + " from invoice: "
                                            + invoiceNo
                            )
                            .data(deletedItem)
                            .success(true)
                            .build()
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .success(false)
                                    .build()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            APIResponse.<Items>builder()
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .success(false)
                                    .build()
                    );
        }
    }








}
