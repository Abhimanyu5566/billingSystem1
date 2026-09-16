/*
package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.dto.createDTO.ItemsRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.InvoiceItemsService;
import com.project.billingManagementSystem.service.InvoiceService;
import jakarta.validation.Valid;
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
    public ResponseEntity<APIResponse<Items>> addInvoiceItems(@Valid @RequestBody ItemsRequest items) {

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
*/


package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.entity.dto.createDTO.ItemsRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.ItemsUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.InvoiceItemsService;
import com.project.billingManagementSystem.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceItemsController {

    private final InvoiceItemsService service;
    private final InvoiceService invoiceService;

    public InvoiceItemsController(
            InvoiceItemsService service,
            InvoiceService invoiceService) {

        this.service = service;
        this.invoiceService = invoiceService;
    }


    @PostMapping("/add-invoice-item/{invoiceNo}")
    public ResponseEntity<APIResponse<Items>> addInvoiceItem(
            @PathVariable String invoiceNo,
            @Valid @RequestBody ItemsRequest request) {

        try {

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invoice No. is required")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Check invoice exists
            // -------------------------------------------------

            invoiceService.findInvoiceNO(invoiceNo);


            // -------------------------------------------------
            // Add item
            // -------------------------------------------------

            Items savedItem =
                    service.addItems(
                            invoiceNo,
                            request
                    );


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            APIResponse.<Items>builder()
                                    .success(true)
                                    .code(HttpStatus.CREATED.value())
                                    .message(
                                            "Successfully added item to invoice: "
                                                    + invoiceNo
                                    )
                                    .data(savedItem)
                                    .build()
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }


    // =========================================================
    // ADD MULTIPLE INVOICE ITEMS
    // =========================================================

    @PostMapping("/add-invoice-items/{invoiceNo}")
    public ResponseEntity<APIResponse<List<Items>>> addInvoiceItems(
            @PathVariable String invoiceNo,
            @Valid @RequestBody List<ItemsRequest> requests) {

        try {

            // -------------------------------------------------
            // Validate invoice number
            // -------------------------------------------------

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<List<Items>>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invoice No. is required")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Validate item list
            // -------------------------------------------------

            if (requests == null || requests.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<List<Items>>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Item list cannot be empty")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Check invoice exists
            // -------------------------------------------------

            invoiceService.findInvoiceNO(invoiceNo);


            // -------------------------------------------------
            // Add items
            // -------------------------------------------------

            List<Items> savedItems =
                    service.addItems(
                            invoiceNo,
                            requests
                    );


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            APIResponse.<List<Items>>builder()
                                    .success(true)
                                    .code(HttpStatus.CREATED.value())
                                    .message(
                                            "Successfully added "
                                                    + savedItems.size()
                                                    + " items to invoice: "
                                                    + invoiceNo
                                    )
                                    .data(savedItems)
                                    .build()
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            APIResponse.<List<Items>>builder()
                                    .success(false)
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            APIResponse.<List<Items>>builder()
                                    .success(false)
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }


    // =========================================================
    // UPDATE INVOICE ITEM
    // =========================================================

    @PutMapping("/update-invoiceItem/{invoiceNo}/{serialNo}")
    public ResponseEntity<APIResponse<Items>> updateInvoiceItem(
            @PathVariable String invoiceNo,
            @PathVariable Integer serialNo,
            @Valid @RequestBody ItemsUpdateRequest request) {

        try {

            // -------------------------------------------------
            // Validate invoice number
            // -------------------------------------------------

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invoice No. is required")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Validate serial number
            // -------------------------------------------------

            if (serialNo == null || serialNo <= 0) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message(
                                                "Valid Item Serial No. is required"
                                        )
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Validate request
            // -------------------------------------------------

            if (request == null) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Item update data is required")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Update item
            // -------------------------------------------------

            Items updatedItem =
                    service.updateInvoiceItems(
                            invoiceNo,
                            serialNo,
                            request
                    );


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            return ResponseEntity.ok(
                    APIResponse.<Items>builder()
                            .success(true)
                            .code(HttpStatus.OK.value())
                            .message(
                                    "Successfully updated item Serial No. "
                                            + serialNo
                                            + " in invoice: "
                                            + invoiceNo
                            )
                            .data(updatedItem)
                            .build()
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }


    // =========================================================
    // DELETE INVOICE ITEM
    // =========================================================

    @DeleteMapping("/delete-invoiceItem/{invoiceNo}/{serialNo}")
    public ResponseEntity<APIResponse<Items>> deleteInvoiceItem(
            @PathVariable String invoiceNo,
            @PathVariable Integer serialNo) {

        try {

            // -------------------------------------------------
            // Validate invoice number
            // -------------------------------------------------

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message("Invoice No. is required")
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Validate serial number
            // -------------------------------------------------

            if (serialNo == null || serialNo <= 0) {

                return ResponseEntity.badRequest()
                        .body(
                                APIResponse.<Items>builder()
                                        .success(false)
                                        .code(HttpStatus.BAD_REQUEST.value())
                                        .message(
                                                "Valid Item Serial No. is required"
                                        )
                                        .data(null)
                                        .build()
                        );
            }


            // -------------------------------------------------
            // Delete item
            // -------------------------------------------------

            Items deletedItem =
                    service.deleteInvoiceItem(
                            invoiceNo,
                            serialNo
                    );


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            return ResponseEntity.ok(
                    APIResponse.<Items>builder()
                            .success(true)
                            .code(HttpStatus.OK.value())
                            .message(
                                    "Successfully deleted item Serial No. "
                                            + serialNo
                                            + " from invoice: "
                                            + invoiceNo
                            )
                            .data(deletedItem)
                            .build()
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            APIResponse.<Items>builder()
                                    .success(false)
                                    .code(HttpStatus.NOT_FOUND.value())
                                    .message(e.getMessage())
                                    .data(null)
                                    .build()
                    );
        }
    }
}
