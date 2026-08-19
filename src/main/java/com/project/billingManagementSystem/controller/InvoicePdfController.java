package com.project.billingManagementSystem.controller;


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

import java.util.Map;

@RestController
@RequestMapping("/invoic-pdf")
public class InvoicePdfController {


    @Autowired
    private InvoicePdfService invoicePdfService;


    @GetMapping(
        value = "/{invoiceNo}/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
)
public ResponseEntity<?> downloadInvoicePdf(
        @PathVariable String invoiceNo
) {

    try {

        byte[] pdf =
                invoicePdfService.generateInvoicePdf(invoiceNo);

        if (pdf == null || pdf.length == 0) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            Map.of(
                                    "status", 404,
                                    "message",
                                    "Invoice not found with invoice number: "
                                            + invoiceNo
                            )
                    );
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Invoice-"
                                + invoiceNo
                                + ".pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "status", 404,
                                "message",
                                "Invoice not found with invoice number: "
                                        + invoiceNo
                        )
                );
    }
}

    @GetMapping(
            value = "/{invoiceNo}/pdf/view",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<?> viewInvoicePdf(
            @PathVariable String invoiceNo
    ) {

        try {

            byte[] pdf =
                    invoicePdfService.generateInvoicePdf(invoiceNo);

            if (pdf == null || pdf.length == 0) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                Map.of(
                                        "status", 404,
                                        "message",
                                        "Invoice not found with invoice number: "
                                                + invoiceNo
                                )
                        );
            }

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=Invoice-"
                                    + invoiceNo
                                    + ".pdf"
                    )
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(pdf);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(
                            Map.of(
                                    "status", 404,
                                    "message",
                                    "Invoice not found with invoice number: "
                                            + invoiceNo
                            )
                    );
        }
    }


}


