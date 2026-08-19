    package com.project.billingManagementSystem.service.pdf.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.InvoiceService;
import com.project.billingManagementSystem.service.pdf.InvoicePdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
public class InvoicePdfServiceImpl implements InvoicePdfService {

    @Autowired
    private InvoiceService invoiceService;


    // =========================================================
    // COLORS
    // =========================================================

    private static final Color PRIMARY_COLOR =
            new Color(31, 78, 121);

    private static final Color SECONDARY_COLOR =
            new Color(236, 242, 248);

    private static final Color LIGHT_GRAY =
            new Color(245, 247, 250);

    private static final Color BORDER_COLOR =
            new Color(210, 210, 210);

    private static final Color DARK_GRAY =
            new Color(80, 80, 80);

    private static final Color GREEN_COLOR =
            new Color(39, 125, 75);

    private static final Color ORANGE_COLOR =
            new Color(220, 140, 30);


    // =========================================================
    // GENERATE PDF
    // =========================================================

    @Override
    public byte[] generateInvoicePdf(String invoiceNo) {

        Invoice invoice =
                invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {

            throw new RuntimeException(
                    "Invoice not found with invoiceNo: "
                            + invoiceNo
            );
        }

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            // =================================================
            // A4 PORTRAIT
            // =================================================

            Document document =
                    new Document(
                            PageSize.A4,
                            30,
                            30,
                            70,
                            55
                    );


            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            outputStream
                    );


            // =================================================
            // HEADER + FOOTER EVENT
            // =================================================

            writer.setPageEvent(
                    new InvoicePageEvent()
            );


            document.open();


            // =================================================
            // FONTS
            // =================================================

            Font normalFont =
                    new Font(
                            Font.HELVETICA,
                            9,
                            Font.NORMAL,
                            DARK_GRAY
                    );


            Font boldFont =
                    new Font(
                            Font.HELVETICA,
                            9,
                            Font.BOLD,
                            DARK_GRAY
                    );


            Font tableHeaderFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD,
                            Color.WHITE
                    );


            Font totalFont =
                    new Font(
                            Font.HELVETICA,
                            12,
                            Font.BOLD,
                            PRIMARY_COLOR
                    );


            // =================================================
            // CUSTOMER + INVOICE DETAILS
            // =================================================

            PdfPTable customerTable =
                    new PdfPTable(2);

            customerTable.setWidthPercentage(100);

            customerTable.setWidths(
                    new float[]{55, 45}
            );


            // =================================================
            // CUSTOMER INFORMATION
            // =================================================

            PdfPCell customerCell =
                    createSectionCell(
                            "BILL TO"
                    );


            Customer customer =
                    invoice.getCustomer();


            if (customer != null) {

                // ---------------------------------------------
                // CUSTOMER NAME
                // ---------------------------------------------

                String customerName =
                        buildCustomerName(
                                customer.getCustomerName(),
                                customer.getCustomerLastName()
                        );


                customerCell.addElement(
                        new Paragraph(
                                customerName,
                                boldFont
                        )
                );


                // ---------------------------------------------
                // PHONE
                // ---------------------------------------------

                if (customer.getPhoneNo() != null
                        && !customer.getPhoneNo().isBlank()) {

                    customerCell.addElement(
                            new Paragraph(
                                    "Phone: "
                                            + customer.getPhoneNo(),
                                    normalFont
                            )
                    );
                }


                // ---------------------------------------------
                // EMAIL
                // ---------------------------------------------

                if (customer.getEmailId() != null
                        && !customer.getEmailId().isBlank()) {

                    customerCell.addElement(
                            new Paragraph(
                                    "Email: "
                                            + customer.getEmailId(),
                                    normalFont
                            )
                    );
                }


                // ---------------------------------------------
                // CUSTOMER STATUS
                // ---------------------------------------------

//                if (customer.getStatus() != null) {
//
//                    customerCell.addElement(
//                            new Paragraph(
//                                    "Customer Status: "
//                                            + customer.getStatus(),
//                                    normalFont
//                            )
//                    );
//                }

            } else {

                customerCell.addElement(
                        new Paragraph(
                                "Customer information not available",
                                normalFont
                        )
                );
            }


            // =================================================
            // INVOICE DETAILS
            // =================================================

            PdfPCell invoiceDetailsCell =
                    createSectionCell(
                            "INVOICE DETAILS"
                    );


            // ---------------------------------------------
            // INVOICE NUMBER
            // ---------------------------------------------

            invoiceDetailsCell.addElement(
                    new Paragraph(
                            "Invoice No: " + invoiceNo,
                            boldFont
                    )
            );


            // ---------------------------------------------
            // INVOICE DATE
            // ---------------------------------------------

            invoiceDetailsCell.addElement(
                    new Paragraph(
                            "Invoice Date: "
                                    + getCurrentDate(),
                            normalFont
                    )
            );


            // ---------------------------------------------
            // PAYMENT STATUS
            // ---------------------------------------------

            String paymentStatus =
                    getPaymentStatus(invoice);


            Font statusFont;


            if ("PAID".equalsIgnoreCase(paymentStatus)) {

                statusFont =
                        new Font(
                                Font.HELVETICA,
                                10,
                                Font.BOLD,
                                GREEN_COLOR
                        );

            } else if ("PENDING".equalsIgnoreCase(paymentStatus)) {

                statusFont =
                        new Font(
                                Font.HELVETICA,
                                10,
                                Font.BOLD,
                                ORANGE_COLOR
                        );

            } else {

                statusFont =
                        new Font(
                                Font.HELVETICA,
                                10,
                                Font.BOLD,
                                PRIMARY_COLOR
                        );
            }


            Paragraph statusParagraph =
                    new Paragraph(
                            "Payment Status: "
                                    + paymentStatus,
                            statusFont
                    );


            invoiceDetailsCell.addElement(
                    statusParagraph
            );


            // =================================================
            // ADD CUSTOMER + INVOICE DETAILS
            // =================================================

            customerTable.addCell(
                    customerCell
            );

            customerTable.addCell(
                    invoiceDetailsCell
            );


            document.add(
                    customerTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // ITEMS TABLE
            // =================================================

            PdfPTable itemTable =
                    new PdfPTable(5);

            itemTable.setWidthPercentage(100);


            /*
             * SR. NO. IS GENERATED FRESH FOR PDF.
             *
             * Example:
             *
             * 1
             * 2
             * 3
             * 4
             *
             * It does NOT use item.getItemSerialNo().
             */

            itemTable.setWidths(
                    new float[]{
                            9,
                            43,
                            12,
                            18,
                            18
                    }
            );


            // =================================================
            // TABLE HEADER
            // =================================================

            addTableHeader(
                    itemTable,
                    "SR. NO.",
                    tableHeaderFont
            );


            addTableHeader(
                    itemTable,
                    "DESCRIPTION",
                    tableHeaderFont
            );


            addTableHeader(
                    itemTable,
                    "QTY",
                    tableHeaderFont
            );


            addTableHeader(
                    itemTable,
                    "RATE",
                    tableHeaderFont
            );


            addTableHeader(
                    itemTable,
                    "AMOUNT",
                    tableHeaderFont
            );


            // =================================================
            // ITEMS
            // =================================================

            BigDecimal subtotal =
                    BigDecimal.ZERO;


            List<Items> items =
                    invoice.getItems();


            // Fresh PDF serial number
            int srNo = 1;


            if (items != null && !items.isEmpty()) {

                for (Items item : items) {


                    // -----------------------------------------
                    // FRESH SR. NO.
                    // -----------------------------------------

                    addItemCell(
                            itemTable,
                            String.valueOf(srNo++),
                            Element.ALIGN_CENTER
                    );


                    // -----------------------------------------
                    // DESCRIPTION
                    // -----------------------------------------

                    addItemCell(
                            itemTable,
                            item.getDescription(),
                            Element.ALIGN_LEFT
                    );


                    // -----------------------------------------
                    // QUANTITY
                    // -----------------------------------------

                    addItemCell(
                            itemTable,
                            item.getQuantity() != null
                                    ? String.valueOf(
                                    item.getQuantity()
                            )
                                    : "0",
                            Element.ALIGN_CENTER
                    );


                    // -----------------------------------------
                    // RATE
                    // -----------------------------------------

                    addItemCell(
                            itemTable,
                            formatCurrency(
                                    item.getRate()
                            ),
                            Element.ALIGN_RIGHT
                    );


                    // -----------------------------------------
                    // AMOUNT
                    // -----------------------------------------

                    addItemCell(
                            itemTable,
                            formatCurrency(
                                    item.getAmount()
                            ),
                            Element.ALIGN_RIGHT
                    );


                    // -----------------------------------------
                    // SUBTOTAL
                    // -----------------------------------------

                    if (item.getAmount() != null) {

                        subtotal =
                                subtotal.add(
                                        item.getAmount()
                                );
                    }
                }

            } else {

                PdfPCell emptyCell =
                        new PdfPCell(
                                new Paragraph(
                                        "No items available",
                                        normalFont
                                )
                        );

                emptyCell.setColspan(5);

                emptyCell.setPadding(10);

                emptyCell.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                itemTable.addCell(
                        emptyCell
                );
            }


            document.add(
                    itemTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // TOTAL SECTION
            // =================================================

            PdfPTable totalTable =
                    new PdfPTable(2);

            totalTable.setWidthPercentage(45);

            totalTable.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            totalTable.setWidths(
                    new float[]{60, 40}
            );


            // SUBTOTAL

            addTotalRow(
                    totalTable,
                    "SUBTOTAL",
                    formatCurrency(subtotal),
                    normalFont,
                    false
            );


            // GRAND TOTAL

            BigDecimal grandTotal =
                    invoice.getTotalAmount() != null
                            ? invoice.getTotalAmount()
                            : subtotal;


            addTotalRow(
                    totalTable,
                    "TOTAL",
                    formatCurrency(grandTotal),
                    totalFont,
                    true
            );


            document.add(
                    totalTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // AMOUNT IN WORDS
            // =================================================

            PdfPCell amountWordsCell =
                    new PdfPCell();


            amountWordsCell.setPadding(10);

            amountWordsCell.setBackgroundColor(
                    LIGHT_GRAY
            );

            amountWordsCell.setBorderColor(
                    BORDER_COLOR
            );


            Paragraph amountWords =
                    new Paragraph(
                            "Amount in Words: "
                                    + amountInWords(
                                    grandTotal
                            ),
                            boldFont
                    );


            amountWordsCell.addElement(
                    amountWords
            );


            PdfPTable amountWordsTable =
                    new PdfPTable(1);

            amountWordsTable.setWidthPercentage(100);

            amountWordsTable.addCell(
                    amountWordsCell
            );


            document.add(
                    amountWordsTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // PAYMENT INFORMATION + TERMS
            // =================================================

            PdfPTable paymentTable =
                    new PdfPTable(2);

            paymentTable.setWidthPercentage(100);

            paymentTable.setWidths(
                    new float[]{60, 40}
            );


            // =================================================
            // PAYMENT INFORMATION
            // =================================================

            PdfPCell paymentCell =
                    createSectionCell(
                            "PAYMENT INFORMATION"
                    );


            paymentCell.addElement(
                    new Paragraph(
                            "Bank Name: Your Bank",
                            normalFont
                    )
            );


            paymentCell.addElement(
                    new Paragraph(
                            "Account Name: Your Company",
                            normalFont
                    )
            );


            paymentCell.addElement(
                    new Paragraph(
                            "Account Number: XXXXXXXX",
                            normalFont
                    )
            );


            paymentCell.addElement(
                    new Paragraph(
                            "IFSC: XXXXXXXX",
                            normalFont
                    )
            );


            // =================================================
            // TERMS
            // =================================================

            PdfPCell termsCell =
                    createSectionCell(
                            "TERMS & CONDITIONS"
                    );


            termsCell.addElement(
                    new Paragraph(
                            "• Payment due within agreed terms.",
                            normalFont
                    )
            );


            termsCell.addElement(
                    new Paragraph(
                            "• Please retain this invoice.",
                            normalFont
                    )
            );


            termsCell.addElement(
                    new Paragraph(
                            "• Thank you for your business.",
                            normalFont
                    )
            );


            paymentTable.addCell(
                    paymentCell
            );


            paymentTable.addCell(
                    termsCell
            );


            document.add(
                    paymentTable
            );


            // =================================================
            // CLOSE DOCUMENT
            // =================================================

            document.close();


            return outputStream.toByteArray();


        } catch (Exception e) {

            throw new RuntimeException(
                    "Error while generating invoice PDF",
                    e
            );
        }
    }


    // =========================================================
    // HEADER + FOOTER PAGE EVENT
    // =========================================================

    private static class InvoicePageEvent
            extends PdfPageEventHelper {


        // No invoiceNo here.
        // Invoice number is now shown inside
        // INVOICE DETAILS section.


        @Override
        public void onEndPage(
                PdfWriter writer,
                Document document
        ) {

            PdfContentByte canvas =
                    writer.getDirectContent();


            // =================================================
            // PAGE DIMENSIONS
            // =================================================

            float left =
                    document.left();

            float right =
                    document.right();

            float top =
                    document.top();

            float bottom =
                    document.bottom();


            // =================================================
            // HEADER
            // =================================================

            canvas.setColorStroke(
                    PRIMARY_COLOR
            );

            canvas.setLineWidth(2);


            // HEADER LINE

            canvas.moveTo(
                    left,
                    top + 30
            );

            canvas.lineTo(
                    right,
                    top + 30
            );

            canvas.stroke();


            // =================================================
            // COMPANY NAME
            // =================================================

            Font companyFont =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD,
                            PRIMARY_COLOR
                    );


            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Paragraph(
                            "YOUR COMPANY",
                            companyFont
                    ),
                    left,
                    top + 38,
                    0
            );


            // =================================================
            // COMPANY DETAILS
            // =================================================

            Font companyDetailsFont =
                    new Font(
                            Font.HELVETICA,
                            7,
                            Font.NORMAL,
                            DARK_GRAY
                    );


            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Paragraph(
                            "Your Business Address | "
                                    + "Mumbai, Maharashtra | "
                                    + "Phone: +91 XXXXX XXXXX | "
                                    + "Email: business@example.com",
                            companyDetailsFont
                    ),
                    left,
                    top + 20,
                    0
            );


            // =================================================
            // INVOICE TITLE
            // =================================================

            Font invoiceFont =
                    new Font(
                            Font.HELVETICA,
                            22,
                            Font.BOLD,
                            PRIMARY_COLOR
                    );


            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    new Paragraph(
                            "INVOICE",
                            invoiceFont
                    ),
                    right,
                    top + 32,
                    0
            );


            /*
             * IMPORTANT:
             *
             * Invoice Number is intentionally NOT printed
             * here.
             *
             * It is now displayed inside:
             *
             * INVOICE DETAILS
             *
             * Invoice No: INV-001
             * Invoice Date: ...
             * Payment Status: ...
             */


            // =================================================
            // FOOTER
            // =================================================

            canvas.setColorStroke(
                    PRIMARY_COLOR
            );

            canvas.setLineWidth(1);


            // FOOTER LINE

            canvas.moveTo(
                    left,
                    bottom - 20
            );

            canvas.lineTo(
                    right,
                    bottom - 20
            );

            canvas.stroke();


            // =================================================
            // FOOTER THANK YOU
            // =================================================

            Font footerFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD,
                            PRIMARY_COLOR
                    );


            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Paragraph(
                            "Thank you for choosing YOUR COMPANY",
                            footerFont
                    ),
                    left,
                    bottom - 35,
                    0
            );


            // =================================================
            // PAGE NUMBER
            // =================================================

            Font pageFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.NORMAL,
                            DARK_GRAY
                    );


            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    new Paragraph(
                            "Page "
                                    + writer.getPageNumber(),
                            pageFont
                    ),
                    right,
                    bottom - 35,
                    0
            );
        }
    }


    // =========================================================
    // CUSTOMER NAME
    // =========================================================

    private String buildCustomerName(
            String firstName,
            String lastName
    ) {

        StringBuilder name =
                new StringBuilder();


        if (firstName != null
                && !firstName.isBlank()) {

            name.append(
                    firstName.trim()
            );
        }


        if (lastName != null
                && !lastName.isBlank()) {

            if (!name.isEmpty()) {

                name.append(" ");
            }

            name.append(
                    lastName.trim()
            );
        }


        if (name.isEmpty()) {

            return "Customer";
        }


        return name.toString();
    }


    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    private String getPaymentStatus(
            Invoice invoice
    ) {

        /*
         * If Invoice has:
         *
         * private PaymentStatus status;
         *
         * this will use invoice.getStatus().
         *
         * Otherwise it will return PENDING.
         */

        try {

            if (invoice.getStatus() != null) {

                return invoice
                        .getStatus()
                        .toString()
                        .toUpperCase();
            }

        } catch (Exception ignored) {

            // Default status
        }


        return "PENDING";
    }


    // =========================================================
    // SECTION CELL
    // =========================================================

    private PdfPCell createSectionCell(
            String title
    ) {

        PdfPCell cell =
                new PdfPCell();


        cell.setPadding(10);


        cell.setBackgroundColor(
                LIGHT_GRAY
        );


        cell.setBorderColor(
                BORDER_COLOR
        );


        Font titleFont =
                new Font(
                        Font.HELVETICA,
                        9,
                        Font.BOLD,
                        PRIMARY_COLOR
                );


        Paragraph titleParagraph =
                new Paragraph(
                        title,
                        titleFont
                );


        titleParagraph.setSpacingAfter(
                5
        );


        cell.addElement(
                titleParagraph
        );


        return cell;
    }


    // =========================================================
    // TABLE HEADER
    // =========================================================

    private void addTableHeader(
            PdfPTable table,
            String text,
            Font font
    ) {

        PdfPCell cell =
                new PdfPCell(
                        new Paragraph(
                                text,
                                font
                        )
                );


        cell.setBackgroundColor(
                PRIMARY_COLOR
        );


        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );


        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );


        cell.setPadding(7);


        cell.setBorderColor(
                PRIMARY_COLOR
        );


        table.addCell(
                cell
        );
    }


    // =========================================================
    // ITEM CELL
    // =========================================================

    private void addItemCell(
            PdfPTable table,
            String text,
            int alignment
    ) {

        Font itemFont =
                new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL,
                        DARK_GRAY
                );


        PdfPCell cell =
                new PdfPCell(
                        new Paragraph(
                                text != null
                                        ? text
                                        : "",
                                itemFont
                        )
                );


        cell.setPadding(6);


        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );


        cell.setHorizontalAlignment(
                alignment
        );


        cell.setBorderColor(
                BORDER_COLOR
        );


        table.addCell(
                cell
        );
    }


    // =========================================================
    // TOTAL ROW
    // =========================================================

    private void addTotalRow(
            PdfPTable table,
            String label,
            String value,
            Font font,
            boolean highlight
    ) {

        PdfPCell labelCell =
                new PdfPCell(
                        new Paragraph(
                                label,
                                font
                        )
                );


        PdfPCell valueCell =
                new PdfPCell(
                        new Paragraph(
                                value,
                                font
                        )
                );


        labelCell.setPadding(7);

        valueCell.setPadding(7);


        labelCell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );


        valueCell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );


        labelCell.setBorderColor(
                BORDER_COLOR
        );


        valueCell.setBorderColor(
                BORDER_COLOR
        );


        if (highlight) {

            labelCell.setBackgroundColor(
                    SECONDARY_COLOR
            );

            valueCell.setBackgroundColor(
                    SECONDARY_COLOR
            );
        }


        table.addCell(
                labelCell
        );


        table.addCell(
                valueCell
        );
    }


    // =========================================================
    // CURRENCY
    // =========================================================

    private String formatCurrency(
            BigDecimal amount
    ) {

        if (amount == null) {

            return "₹ 0.00";
        }


        return "₹ "
                + String.format(
                "%,.2f",
                amount
        );
    }


    // =========================================================
    // DATE
    // =========================================================

    private String getCurrentDate() {

        return LocalDate.now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "dd MMM yyyy"
                        )
                );
    }


    // =========================================================
    // AMOUNT IN WORDS
    // =========================================================

    private String amountInWords(
            BigDecimal amount
    ) {

        if (amount == null) {

            return "Zero Rupees Only";
        }


        long rupees =
                amount.longValue();


        return convertNumberToWords(
                rupees
        )
                + " Rupees Only";
    }


    // =========================================================
    // NUMBER TO WORDS
    // =========================================================

    private String convertNumberToWords(
            long number
    ) {

        if (number == 0) {

            return "Zero";
        }


        if (number < 0) {

            return "Minus "
                    + convertNumberToWords(
                    -number
            );
        }


        String[] units = {

                "",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
                "Eight",
                "Nine",
                "Ten",
                "Eleven",
                "Twelve",
                "Thirteen",
                "Fourteen",
                "Fifteen",
                "Sixteen",
                "Seventeen",
                "Eighteen",
                "Nineteen"
        };


        String[] tens = {

                "",
                "",
                "Twenty",
                "Thirty",
                "Forty",
                "Fifty",
                "Sixty",
                "Seventy",
                "Eighty",
                "Ninety"
        };


        if (number < 20) {

            return units[(int) number];
        }


        if (number < 100) {

            return tens[(int) number / 10]
                    + (
                    number % 10 != 0
                            ? " "
                              + units[
                                (int) number % 10
                                ]
                            : ""
            );
        }


        if (number < 1000) {

            return units[
                    (int) number / 100
                    ]
                    + " Hundred"
                    + (
                    number % 100 != 0
                            ? " "
                              + convertNumberToWords(
                            number % 100
                    )
                            : ""
            );
        }


        if (number < 100000) {

            return convertNumberToWords(
                    number / 1000
            )
                    + " Thousand"
                    + (
                    number % 1000 != 0
                            ? " "
                              + convertNumberToWords(
                            number % 1000
                    )
                            : ""
            );
        }


        if (number < 10000000) {

            return convertNumberToWords(
                    number / 100000
            )
                    + " Lakh"
                    + (
                    number % 100000 != 0
                            ? " "
                              + convertNumberToWords(
                            number % 100000
                    )
                            : ""
            );
        }


        return convertNumberToWords(
                number / 10000000
        )
                + " Crore"
                + (
                number % 10000000 != 0
                        ? " "
                          + convertNumberToWords(
                        number % 10000000
                )
                        : ""
        );
    }
}