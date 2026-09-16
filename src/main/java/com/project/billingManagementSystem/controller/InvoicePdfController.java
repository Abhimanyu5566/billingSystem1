/*
package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.service.pdf.InvoicePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice-pdf")
public class InvoicePdfController {

    @Autowired
    private InvoicePdfService invoicePdfService;


    // =========================================================
    // DOWNLOAD INVOICE PDF
    // =========================================================

    @GetMapping(value = "/{invoiceNo}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadInvoicePdf(@PathVariable String invoiceNo) {

        try {

            // -------------------------------------------------
            // VALIDATE INVOICE NUMBER
            // -------------------------------------------------

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .body(APIResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message("Invoice number is required").build());
            }


            // -------------------------------------------------
            // GENERATE PDF
            // -------------------------------------------------

            byte[] pdf = invoicePdfService.generateInvoicePdf(invoiceNo.trim());


            // -------------------------------------------------
            // PDF NOT FOUND
            // -------------------------------------------------

            if (pdf == null || pdf.length == 0) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(APIResponse.builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Invoice not present in DB: " + invoiceNo).build());
            }

            // -------------------------------------------------
            // DOWNLOAD PDF
            // -------------------------------------------------

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Invoice-" + invoiceNo.trim() + ".pdf").contentType(MediaType.APPLICATION_PDF).body(pdf);


        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(APIResponse.builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Invoice not present in DB: " + invoiceNo).build());
        }
    }


    // =========================================================
    // VIEW INVOICE PDF
    // =========================================================

    @GetMapping(value = "/{invoiceNo}/pdf/view", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> viewInvoicePdf(@PathVariable String invoiceNo) {

        try {

            // -------------------------------------------------
            // VALIDATE INVOICE NUMBER
            // -------------------------------------------------

            if (invoiceNo == null || invoiceNo.isBlank()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(APIResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message("Invoice number is required").build());
            }


            // -------------------------------------------------
            // GENERATE PDF
            // -------------------------------------------------

            byte[] pdf = invoicePdfService.generateInvoicePdf(invoiceNo.trim());


            // -------------------------------------------------
            // PDF NOT FOUND
            // -------------------------------------------------

            if (pdf == null || pdf.length == 0) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(APIResponse.builder().success(false).code(HttpStatus.NOT_FOUND.value()).message("Invoice not present in DB: " + invoiceNo).build());
            }


            // -------------------------------------------------
            // VIEW PDF IN BROWSER
            // -------------------------------------------------

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Invoice-" + invoiceNo.trim() + ".pdf").contentType(MediaType.APPLICATION_PDF).body(pdf);


        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(APIResponse.builder().code(HttpStatus.NOT_FOUND.value()).message("Invoice not present in DB: " + invoiceNo).build());
        }
    }
}
*/

package com.project.billingManagementSystem.controller;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import com.project.billingManagementSystem.service.pdf.InvoicePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice-pdf")
public class InvoicePdfController {

    @Autowired
    private InvoicePdfService invoicePdfService;

    // =========================================================
    // DOWNLOAD INVOICE PDF
    // =========================================================

    @GetMapping("/{invoiceNo}/pdf")
    public ResponseEntity<?> downloadInvoicePdf(@PathVariable String invoiceNo) {

        return generateInvoicePdf(invoiceNo, true);
    }

    // =========================================================
    // VIEW INVOICE PDF
    // =========================================================

    @GetMapping("/{invoiceNo}/pdf/view")
    public ResponseEntity<?> viewInvoicePdf(@PathVariable String invoiceNo) {

        return generateInvoicePdf(invoiceNo, false);
    }

    // =========================================================
    // COMMON PDF GENERATION METHOD
    // =========================================================

    private ResponseEntity<?> generateInvoicePdf(String invoiceNo, boolean download) {

        // -------------------------------------------------
        // VALIDATE INVOICE NUMBER
        // -------------------------------------------------

        if (invoiceNo == null || invoiceNo.isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            APIResponse.builder()
                                    .success(false)
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message("Invoice number is required")
                                    .build()
                    );
        }

        String trimmedInvoiceNo = invoiceNo.trim();

        try {

            // -------------------------------------------------
            // GENERATE PDF
            // -------------------------------------------------

            byte[] pdf = invoicePdfService.generateInvoicePdf(trimmedInvoiceNo);

            // -------------------------------------------------
            // PDF NOT FOUND
            // -------------------------------------------------

            if (pdf == null || pdf.length == 0) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                APIResponse.builder()
                                        .success(false)
                                        .code(HttpStatus.NOT_FOUND.value())
                                        .message("Invoice not present in DB: " + trimmedInvoiceNo)
                                        .build()
                        );
            }

            // -------------------------------------------------
            // CONTENT DISPOSITION
            // -------------------------------------------------

            String disposition = download
                    ? "attachment; filename=\"Invoice-" + trimmedInvoiceNo + ".pdf\""
                    : "inline; filename=\"Invoice-" + trimmedInvoiceNo + ".pdf\"";

            // -------------------------------------------------
            // RETURN PDF
            // -------------------------------------------------

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdf.length)
                    .body(pdf);

        } catch (RuntimeException e) {

            // Log the actual exception instead of silently
            // converting every runtime error into 404.

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            APIResponse.builder()
                                    .success(false)
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .message("Failed to generate invoice PDF: " + e.getMessage())
                                    .build()
                    );
        }
    }
}

