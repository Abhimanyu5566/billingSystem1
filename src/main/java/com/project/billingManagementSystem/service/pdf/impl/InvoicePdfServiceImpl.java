/*
package com.project.billingManagementSystem.service.pdf.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.CompanyService;
import com.project.billingManagementSystem.service.InvoiceService;
import com.project.billingManagementSystem.service.pdf.InvoicePdfService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class InvoicePdfServiceImpl implements InvoicePdfService {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CompanyService companyService;

    private static final Color PRIMARY = new Color(31, 78, 121);

    private static final Color LIGHT_BLUE = new Color(236, 242, 248);

    private static final Color LIGHT_GRAY = new Color(245, 247, 250);

    private static final Color BORDER = new Color(210, 210, 210);

    private static final Color DARK = new Color(70, 70, 70);

    private static final Color GREEN = new Color(39, 125, 75);

    private static final Color ORANGE = new Color(220, 140, 30);

    @Override
    public byte[] generateInvoicePdf(String invoiceNo) {

        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {
            throw new RuntimeException("Invoice not found with invoiceNo: " + invoiceNo);
        }

        Company company = companyService.getCompany();

        if (company == null) {
            throw new RuntimeException("Company details are not configured.");
        }

        try {

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 30, 30, 65, 50);

            PdfWriter writer = PdfWriter.getInstance(document, output);

            writer.setPageEvent(new InvoicePageEvent(company));

            document.open();

            Font normal = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK);

            Font bold = new Font(Font.HELVETICA, 8, Font.BOLD, DARK);

            Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);

            Font totalFont = new Font(Font.HELVETICA, 9, Font.BOLD, PRIMARY);


            PdfPTable customerTable = new PdfPTable(2);

            customerTable.setWidthPercentage(100);

            customerTable.setWidths(new float[]{55, 45});


            PdfPCell customerCell = sectionCell("BILL TO");


            Customer customer = invoice.getCustomer();

            if (customer != null) {

                customerCell.addElement(new Paragraph(buildCustomerName(customer.getCustomerName(), customer.getCustomerLastName()), bold));

                if (customer.getPhoneNo() != null && !customer.getPhoneNo().isBlank()) {

                    customerCell.addElement(new Paragraph("Phone: " + customer.getPhoneNo(), normal));
                }

                if (customer.getEmailId() != null && !customer.getEmailId().isBlank()) {

                    customerCell.addElement(new Paragraph("Email: " + customer.getEmailId(), normal));
                }

            } else {

                customerCell.addElement(new Paragraph("Customer information not available", normal));
            }


            PdfPCell invoiceCell = sectionCell("INVOICE DETAILS");


            invoiceCell.addElement(new Paragraph("Invoice No: " + invoiceNo, bold));

            invoiceCell.addElement(new Paragraph("Invoice Date: " + formatDate(invoice.getDate()), normal));


            String status = getPaymentStatus(invoice);


            Color statusColor = "PAID".equalsIgnoreCase(status) ? GREEN : ORANGE;


            invoiceCell.addElement(new Paragraph("Payment Status: " + status, new Font(Font.HELVETICA, 8, Font.BOLD, statusColor)));


            customerTable.addCell(customerCell);

            customerTable.addCell(invoiceCell);


            document.add(customerTable);


            document.add(new Paragraph(" "));

            PdfPTable itemTable = new PdfPTable(5);

            itemTable.setWidthPercentage(100);

            itemTable.setWidths(new float[]{8, 44, 12, 18, 18});


            addHeader(itemTable, "SR.", headerFont);

            addHeader(itemTable, "DESCRIPTION", headerFont);

            addHeader(itemTable, "QTY", headerFont);

            addHeader(itemTable, "RATE", headerFont);

            addHeader(itemTable, "AMOUNT", headerFont);


            List<Items> items = invoice.getItems();


            int sr = 1;


            if (items != null && !items.isEmpty()) {

                for (Items item : items) {

                    addItem(itemTable, String.valueOf(sr++), Element.ALIGN_CENTER);

                    addItem(itemTable, item.getDescription(), Element.ALIGN_LEFT);

                    addItem(itemTable, item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "0", Element.ALIGN_CENTER);

                    addItem(itemTable, currency(item.getRate()), Element.ALIGN_RIGHT);

                    addItem(itemTable, currency(item.getAmount()), Element.ALIGN_RIGHT);
                }

            } else {

                PdfPCell cell = new PdfPCell(new Paragraph("No items available", normal));

                cell.setColspan(5);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                itemTable.addCell(cell);
            }


            document.add(itemTable);

            document.add(new Paragraph(" "));


            BigDecimal total = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;


            BigDecimal advance = invoice.getAdvanceAmount() != null ? invoice.getAdvanceAmount() : BigDecimal.ZERO;


            BigDecimal balance = invoice.getBalanceAmount() != null ? invoice.getBalanceAmount() : total.subtract(advance);


            // =================================================
            // QR + SUMMARY TABLE
            // =================================================

            PdfPTable paymentTable = new PdfPTable(2);

            paymentTable.setWidthPercentage(100);

            paymentTable.setWidths(new float[]{43, 57});


            // =================================================
            // QR CELL
            // =================================================

            PdfPCell qrCell = new PdfPCell();

            qrCell.setPadding(6);

            qrCell.setBorderColor(BORDER);

            qrCell.setBackgroundColor(Color.WHITE);

            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph qrTitle = new Paragraph("SCAN TO PAY", new Font(Font.HELVETICA, 8, Font.BOLD, PRIMARY));

            qrTitle.setAlignment(Element.ALIGN_CENTER);

            qrCell.addElement(qrTitle);


            try {

                Image qr = generatePaymentQr(company, balance);

                qr.scaleAbsolute(75, 75);

                qr.setAlignment(Element.ALIGN_CENTER);

                qrCell.addElement(qr);

            } catch (Exception e) {

                Paragraph error = new Paragraph("QR unavailable", normal);

                error.setAlignment(Element.ALIGN_CENTER);

                qrCell.addElement(error);
            }


            // =================================================
            // QR UPI
            // =================================================

            if (company.getUpiId() != null && !company.getUpiId().isBlank()) {

                Paragraph upi = new Paragraph("UPI: " + company.getUpiId(), new Font(Font.HELVETICA, 7, Font.BOLD, DARK));

                upi.setAlignment(Element.ALIGN_CENTER);

                qrCell.addElement(upi);
            }


            // =================================================
            // QR PAY AMOUNT
            // =================================================

            Paragraph qrAmount = new Paragraph("Pay: " + currency(balance), new Font(Font.HELVETICA, 8, Font.BOLD, ORANGE));

            qrAmount.setAlignment(Element.ALIGN_CENTER);

            qrCell.addElement(qrAmount);


            // =================================================
            // SUMMARY CELL
            // =================================================

            PdfPCell summaryCell = new PdfPCell();

            summaryCell.setPadding(0);

            summaryCell.setBorderColor(BORDER);


            PdfPTable summary = new PdfPTable(2);

            summary.setWidthPercentage(100);

            summary.setWidths(new float[]{60, 40});


            // TOTAL

            summaryRow(summary, "TOTAL", currency(total), true, PRIMARY);


            // ADVANCE

            summaryRow(summary, "ADVANCE PAID", currency(advance), false, DARK);


            // BALANCE

            summaryRow(summary, "BALANCE DUE", currency(balance), true, ORANGE);


            // UPI

            if (company.getUpiId() != null && !company.getUpiId().isBlank()) {

                summaryRow(summary, "UPI ID", company.getUpiId(), false, DARK);
            }


            // PAY AMOUNT

            summaryRow(summary, "PAY AMOUNT", currency(balance), false, ORANGE);


            summaryCell.addElement(summary);


            paymentTable.addCell(qrCell);
            paymentTable.addCell(summaryCell);


            document.add(paymentTable);


            document.add(new Paragraph(" "));


            // =================================================
            // AMOUNT IN WORDS
            // =================================================

            PdfPCell wordsCell = new PdfPCell(new Paragraph("Amount in Words: " + amountInWords(total), bold));

            wordsCell.setPadding(8);

            wordsCell.setBackgroundColor(LIGHT_GRAY);

            wordsCell.setBorderColor(BORDER);


            PdfPTable wordsTable = new PdfPTable(1);

            wordsTable.setWidthPercentage(100);

            wordsTable.addCell(wordsCell);


            document.add(wordsTable);


            document.add(new Paragraph(" "));


            // =================================================
            // PAYMENT DETAILS
            // =================================================

            PdfPTable bankTable = new PdfPTable(2);

            bankTable.setWidthPercentage(100);

            bankTable.setWidths(new float[]{60, 40});


            PdfPCell bankCell = sectionCell("PAYMENT INFORMATION");


            if (company.getBankName() != null && !company.getBankName().isBlank()) {

                bankCell.addElement(new Paragraph("Bank Name: " + company.getBankName(), normal));
            }

            if (company.getAccountHolderName() != null && !company.getAccountHolderName().isBlank()) {

                bankCell.addElement(new Paragraph("Account Name: " + company.getAccountHolderName(), normal));
            }

            if (company.getAccountNumber() != null && !company.getAccountNumber().isBlank()) {

                bankCell.addElement(new Paragraph("Account Number: " + company.getAccountNumber(), normal));
            }

            if (company.getIfscCode() != null && !company.getIfscCode().isBlank()) {

                bankCell.addElement(new Paragraph("IFSC: " + company.getIfscCode(), normal));
            }


            PdfPCell termsCell = sectionCell("TERMS & CONDITIONS");


            termsCell.addElement(new Paragraph("• Payment due within agreed terms.", normal));

            termsCell.addElement(new Paragraph("• Please retain this invoice.", normal));

            termsCell.addElement(new Paragraph("• Thank you for your business.", normal));


            bankTable.addCell(bankCell);
            bankTable.addCell(termsCell);


            document.add(bankTable);


            // =================================================
            // CLOSE
            // =================================================

            document.close();

            return output.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException("Error while generating invoice PDF", e);
        }
    }


    // =========================================================
    // PAYMENT QR
    // =========================================================

    private Image generatePaymentQr(Company company, BigDecimal balance) throws Exception {

        String upi = company.getUpiId();

        if (upi == null || upi.isBlank()) {

            throw new IllegalArgumentException("UPI ID is not configured.");
        }


        String name = company.getName() != null ? company.getName() : "Company";


        BigDecimal amount = balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;


        String qrData = "upi://pay" + "?pa=" + URLEncoder.encode(upi, StandardCharsets.UTF_8) + "&pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&am=" + amount.toPlainString() + "&cu=INR";


        BitMatrix matrix = new QRCodeWriter().encode(qrData, BarcodeFormat.QR_CODE, 250, 250);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();


        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);


        return Image.getInstance(stream.toByteArray());
    }


    // =========================================================
    // SUMMARY ROW
    // =========================================================

    private void summaryRow(PdfPTable table, String label, String value, boolean highlight, Color color) {

        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD, color);

        Font valueFont = new Font(Font.HELVETICA, 8, Font.BOLD, color);


        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));


        PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));


        labelCell.setPadding(6);
        valueCell.setPadding(6);


        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);


        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        labelCell.setBorderColor(BORDER);
        valueCell.setBorderColor(BORDER);


        if (highlight) {

            labelCell.setBackgroundColor(LIGHT_BLUE);

            valueCell.setBackgroundColor(LIGHT_BLUE);

        } else {

            labelCell.setBackgroundColor(Color.WHITE);

            valueCell.setBackgroundColor(Color.WHITE);
        }


        table.addCell(labelCell);
        table.addCell(valueCell);
    }


    // =========================================================
    // SECTION CELL
    // =========================================================

    private PdfPCell sectionCell(String title) {

        PdfPCell cell = new PdfPCell();

        cell.setPadding(8);

        cell.setBackgroundColor(LIGHT_GRAY);

        cell.setBorderColor(BORDER);


        Font titleFont = new Font(Font.HELVETICA, 8, Font.BOLD, PRIMARY);


        cell.addElement(new Paragraph(title, titleFont));


        return cell;
    }


    // =========================================================
    // HEADER CELL
    // =========================================================

    private void addHeader(PdfPTable table, String text, Font font) {

        PdfPCell cell = new PdfPCell(new Paragraph(text, font));

        cell.setBackgroundColor(PRIMARY);

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell.setPadding(6);

        cell.setBorderColor(PRIMARY);


        table.addCell(cell);
    }


    // =========================================================
    // ITEM CELL
    // =========================================================

    private void addItem(PdfPTable table, String text, int alignment) {

        Font font = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK);


        PdfPCell cell = new PdfPCell(new Paragraph(text != null ? text : "", font));


        cell.setPadding(5);

        cell.setHorizontalAlignment(alignment);

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell.setBorderColor(BORDER);


        table.addCell(cell);
    }


    // =========================================================
    // CURRENCY
    // =========================================================

    private String currency(BigDecimal amount) {

        if (amount == null) {
            return "₹ 0.00";
        }

        return "₹ " + String.format("%,.2f", amount);
    }


    // =========================================================
    // DATE
    // =========================================================

    private String formatDate(LocalDate date) {

        if (date == null) {
            return "-";
        }

        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }


    // =========================================================
    // CUSTOMER NAME
    // =========================================================

    private String buildCustomerName(String first, String last) {

        StringBuilder name = new StringBuilder();


        if (first != null && !first.isBlank()) {

            name.append(first.trim());
        }


        if (last != null && !last.isBlank()) {

            if (!name.isEmpty()) {
                name.append(" ");
            }

            name.append(last.trim());
        }


        return name.isEmpty() ? "Customer" : name.toString();
    }


    // =========================================================
    // STATUS
    // =========================================================

    private String getPaymentStatus(Invoice invoice) {

        try {

            if (invoice.getStatus() != null) {

                return invoice.getStatus().toString().toUpperCase();
            }

        } catch (Exception ignored) {
        }

        return "PENDING";
    }


    // =========================================================
    // AMOUNT WORDS
    // =========================================================

    private String amountInWords(BigDecimal amount) {

        if (amount == null) {
            return "Zero Rupees Only";
        }

        return convertNumberToWords(amount.longValue()) + " Rupees Only";
    }


    // =========================================================
    // NUMBER TO WORDS
    // =========================================================

    private String convertNumberToWords(long number) {

        if (number == 0) {
            return "Zero";
        }

        if (number < 0) {

            return "Minus " + convertNumberToWords(-number);
        }


        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};


        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};


        if (number < 20) {

            return units[(int) number];
        }


        if (number < 100) {

            return tens[(int) number / 10] + (number % 10 != 0 ? " " + units[(int) number % 10] : "");
        }


        if (number < 1000) {

            return units[(int) number / 100] + " Hundred" + (number % 100 != 0 ? " " + convertNumberToWords(number % 100) : "");
        }


        if (number < 100000) {

            return convertNumberToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + convertNumberToWords(number % 1000) : "");
        }


        if (number < 10000000) {

            return convertNumberToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + convertNumberToWords(number % 100000) : "");
        }


        return convertNumberToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + convertNumberToWords(number % 10000000) : "");
    }


    // =========================================================
    // HEADER / FOOTER
    // =========================================================

    private static class InvoicePageEvent extends PdfPageEventHelper {

        private final Company company;


        public InvoicePageEvent(Company company) {

            this.company = company;
        }


        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte canvas = writer.getDirectContent();


            float left = document.left();

            float right = document.right();

            float top = document.top();

            float bottom = document.bottom();


            // =================================================
            // HEADER LINE
            // =================================================

            canvas.setColorStroke(PRIMARY);

            canvas.setLineWidth(2);


            canvas.moveTo(left, top + 25);

            canvas.lineTo(right, top + 25);

            canvas.stroke();


            // =================================================
            // COMPANY NAME
            // =================================================

            String companyName = company.getName() != null && !company.getName().isBlank() ? company.getName() : "Company";


            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph(companyName, new Font(Font.HELVETICA, 16, Font.BOLD, PRIMARY)), left, top + 32, 0);


            // =================================================
            // INVOICE
            // =================================================

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Paragraph("INVOICE", new Font(Font.HELVETICA, 20, Font.BOLD, PRIMARY)), right, top + 28, 0);


            // =================================================
            // COMPANY DETAILS
            // =================================================

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph(companyDetails(), new Font(Font.HELVETICA, 7, Font.NORMAL, DARK)), left, top + 12, 0);


            // =================================================
            // FOOTER
            // =================================================

            canvas.setColorStroke(PRIMARY);

            canvas.setLineWidth(1);


            canvas.moveTo(left, bottom - 18);

            canvas.lineTo(right, bottom - 18);

            canvas.stroke();


            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph("Thank you for choosing " + companyName, new Font(Font.HELVETICA, 7, Font.BOLD, PRIMARY)), left, bottom - 30, 0);


            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Paragraph("Page " + writer.getPageNumber(), new Font(Font.HELVETICA, 7, Font.NORMAL, DARK)), right, bottom - 30, 0);
        }


        private String companyDetails() {

            StringBuilder text = new StringBuilder();


            if (company.getAddress() != null && !company.getAddress().isBlank()) {

                text.append(company.getAddress().trim());
            }


            if (company.getPhone() != null && !company.getPhone().isBlank()) {

                if (!text.isEmpty()) {
                    text.append(" | ");
                }

                text.append("Phone: " + company.getPhone().trim());
            }


            if (company.getEmail() != null && !company.getEmail().isBlank()) {

                if (!text.isEmpty()) {
                    text.append(" | ");
                }

                text.append("Email: " + company.getEmail().trim());
            }


            if (company.getGstNumber() != null && !company.getGstNumber().isBlank()) {

                if (!text.isEmpty()) {
                    text.append(" | ");
                }

                text.append("GSTIN: " + company.getGstNumber().trim());
            }

            return text.toString();
        }
    }
}
*/


package com.project.billingManagementSystem.service.pdf.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import com.project.billingManagementSystem.entity.BusinessDetails.Company;
import com.project.billingManagementSystem.entity.Customer;
import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.service.CompanyService;
import com.project.billingManagementSystem.service.InvoiceService;
import com.project.billingManagementSystem.service.pdf.InvoicePdfService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class InvoicePdfServiceImpl implements InvoicePdfService {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CompanyService companyService;


    // =========================================================
    // COLORS
    // =========================================================

    private static final Color PRIMARY = new Color(31, 78, 121);

    private static final Color PRIMARY_DARK = new Color(22, 59, 92);

    private static final Color LIGHT_BLUE = new Color(236, 242, 248);

    private static final Color LIGHT_GRAY = new Color(245, 247, 250);

    private static final Color MEDIUM_GRAY = new Color(225, 229, 234);

    private static final Color BORDER = new Color(205, 210, 216);

    private static final Color DARK = new Color(65, 65, 65);

    private static final Color MUTED = new Color(105, 105, 105);

    private static final Color GREEN = new Color(39, 125, 75);

    private static final Color ORANGE = new Color(220, 140, 30);

    private static final Color WHITE = Color.WHITE;


    // =========================================================
    // GENERATE PDF
    // =========================================================

    @Override
    public byte[] generateInvoicePdf(String invoiceNo) {

        if (invoiceNo == null || invoiceNo.isBlank()) {

            throw new IllegalArgumentException("Invoice number is required");
        }

        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo.trim());

        if (invoice == null) {

            throw new RuntimeException("Invoice not found with invoiceNo: " + invoiceNo);
        }

        Company company = companyService.getCompany();

        if (company == null) {

            throw new RuntimeException("Company details are not configured.");
        }


        try {

            ByteArrayOutputStream output = new ByteArrayOutputStream();


            Document document = new Document(PageSize.A4, 30, 30, 75, 55);


            PdfWriter writer = PdfWriter.getInstance(document, output);


            writer.setPageEvent(new InvoicePageEvent(company));


            document.open();


            // =================================================
            // FONTS
            // =================================================

            Font normal = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK);


            Font small = new Font(Font.HELVETICA, 7, Font.NORMAL, MUTED);


            Font bold = new Font(Font.HELVETICA, 8, Font.BOLD, DARK);


            Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, WHITE);


            // =================================================
            // CUSTOMER + INVOICE DETAILS
            // =================================================

            PdfPTable customerTable = new PdfPTable(2);

            customerTable.setWidthPercentage(100);

            customerTable.setWidths(new float[]{55, 45});


            // -------------------------------------------------
            // CUSTOMER
            // -------------------------------------------------

            PdfPCell customerCell = sectionCell("BILL TO");


            Customer customer = invoice.getCustomer();


            if (customer != null) {

                customerCell.addElement(new Paragraph(buildCustomerName(customer.getCustomerName(), customer.getCustomerLastName()), bold));


                if (customer.getPhoneNo() != null && !customer.getPhoneNo().isBlank()) {

                    customerCell.addElement(new Paragraph("Phone: " + customer.getPhoneNo().trim(), normal));
                }


                if (customer.getEmailId() != null && !customer.getEmailId().isBlank()) {

                    customerCell.addElement(new Paragraph("Email: " + customer.getEmailId().trim(), normal));
                }

            } else {

                customerCell.addElement(new Paragraph("Customer information not available", small));
            }


            // -------------------------------------------------
            // INVOICE DETAILS
            // -------------------------------------------------

            PdfPCell invoiceCell = sectionCell("INVOICE DETAILS");


            invoiceCell.addElement(new Paragraph("Invoice No: " + invoiceNo.trim(), bold));


            invoiceCell.addElement(new Paragraph("Invoice Date: " + formatDate(invoice.getDate()), normal));


            String status = getPaymentStatus(invoice);


            Color statusColor = "PAID".equalsIgnoreCase(status) ? GREEN : ORANGE;


            invoiceCell.addElement(new Paragraph("Payment Status: " + status, new Font(Font.HELVETICA, 8, Font.BOLD, statusColor)));


            customerTable.addCell(customerCell);

            customerTable.addCell(invoiceCell);


            document.add(customerTable);


            document.add(createSpacer(7));


            // =================================================
            // ITEMS TABLE
            // =================================================

            PdfPTable itemTable = new PdfPTable(5);

            itemTable.setWidthPercentage(100);

            itemTable.setWidths(new float[]{8, 44, 12, 18, 18});


            addHeader(itemTable, "SR.", headerFont);


            addHeader(itemTable, "DESCRIPTION", headerFont);


            addHeader(itemTable, "QTY", headerFont);


            addHeader(itemTable, "RATE", headerFont);


            addHeader(itemTable, "AMOUNT", headerFont);


            List<Items> items = invoice.getItems();


            int sr = 1;


            if (items != null && !items.isEmpty()) {

                for (Items item : items) {

                    String serial = item.getItemSerialNo() != null ? String.valueOf(item.getItemSerialNo()) : String.valueOf(sr);


                    addItem(itemTable, serial, Element.ALIGN_CENTER);


                    addItem(itemTable, item.getDescription(), Element.ALIGN_LEFT);


                    addItem(itemTable, item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "0", Element.ALIGN_CENTER);


                    addItem(itemTable, currency(item.getRate()), Element.ALIGN_RIGHT);


                    addItem(itemTable, currency(item.getAmount()), Element.ALIGN_RIGHT);


                    sr++;
                }

            } else {

                PdfPCell cell = new PdfPCell(new Paragraph("No items available", normal));

                cell.setColspan(5);

                cell.setPadding(10);

                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                cell.setBackgroundColor(LIGHT_GRAY);

                cell.setBorderColor(BORDER);

                itemTable.addCell(cell);
            }


            document.add(itemTable);


            document.add(createSpacer(8));


            // =================================================
            // AMOUNTS
            // =================================================

            BigDecimal total = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;


            BigDecimal advance = invoice.getAdvanceAmount() != null ? invoice.getAdvanceAmount() : BigDecimal.ZERO;


            BigDecimal balance = invoice.getBalanceAmount() != null ? invoice.getBalanceAmount() : total.subtract(advance);


            total = total.setScale(2, RoundingMode.HALF_UP);


            advance = advance.setScale(2, RoundingMode.HALF_UP);


            balance = balance.setScale(2, RoundingMode.HALF_UP);


            // =================================================
            // QR + SUMMARY
            // =================================================

            PdfPTable paymentTable = new PdfPTable(2);

            paymentTable.setWidthPercentage(100);

            paymentTable.setWidths(new float[]{43, 57});


            // =================================================
            // QR CELL
            // =================================================

            PdfPCell qrCell = new PdfPCell();

            qrCell.setPadding(8);

            qrCell.setBorderColor(BORDER);

            qrCell.setBackgroundColor(WHITE);

            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph qrTitle = new Paragraph("SCAN TO PAY", new Font(Font.HELVETICA, 8, Font.BOLD, PRIMARY));


            qrTitle.setAlignment(Element.ALIGN_CENTER);


            qrCell.addElement(qrTitle);


            try {

                Image qr = generatePaymentQr(company, balance);


                qr.scaleAbsolute(82, 82);


                qr.setAlignment(Element.ALIGN_CENTER);


                qrCell.addElement(qr);

            } catch (Exception e) {

                Paragraph error = new Paragraph("QR unavailable", small);


                error.setAlignment(Element.ALIGN_CENTER);


                qrCell.addElement(error);
            }


            // -------------------------------------------------
            // UPI
            // -------------------------------------------------

            if (company.getUpiId() != null && !company.getUpiId().isBlank()) {

                Paragraph upi = new Paragraph("UPI: " + company.getUpiId().trim(), new Font(Font.HELVETICA, 7, Font.BOLD, DARK));


                upi.setAlignment(Element.ALIGN_CENTER);


                qrCell.addElement(upi);
            }


            // -------------------------------------------------
            // PAY AMOUNT
            // -------------------------------------------------

            Paragraph qrAmount = new Paragraph("Pay: " + currency(balance), new Font(Font.HELVETICA, 8, Font.BOLD, ORANGE));


            qrAmount.setAlignment(Element.ALIGN_CENTER);


            qrCell.addElement(qrAmount);


            // =================================================
            // SUMMARY CELL
            // =================================================

            PdfPCell summaryCell = new PdfPCell();


            summaryCell.setPadding(0);

            summaryCell.setBorderColor(BORDER);


            PdfPTable summary = new PdfPTable(2);


            summary.setWidthPercentage(100);


            summary.setWidths(new float[]{60, 40});


            // TOTAL

            summaryRow(summary, "TOTAL", currency(total), true, PRIMARY);


            // ADVANCE

            summaryRow(summary, "ADVANCE PAID", currency(advance), false, DARK);


            // BALANCE

            summaryRow(summary, "BALANCE DUE", currency(balance), true, ORANGE);


            // UPI

            if (company.getUpiId() != null && !company.getUpiId().isBlank()) {

                summaryRow(summary, "UPI ID", company.getUpiId().trim(), false, DARK);
            }


            // PAY AMOUNT

            summaryRow(summary, "PAY AMOUNT", currency(balance), false, ORANGE);


            summaryCell.addElement(summary);


            paymentTable.addCell(qrCell);

            paymentTable.addCell(summaryCell);


            document.add(paymentTable);


            document.add(createSpacer(8));


            // =================================================
            // AMOUNT IN WORDS
            // =================================================

            PdfPTable wordsTable = new PdfPTable(1);

            wordsTable.setWidthPercentage(100);


            PdfPCell wordsCell = new PdfPCell(new Paragraph("Amount in Words: " + amountInWords(total), bold));


            wordsCell.setPadding(8);

            wordsCell.setBackgroundColor(LIGHT_GRAY);

            wordsCell.setBorderColor(BORDER);


            wordsTable.addCell(wordsCell);


            document.add(wordsTable);


            document.add(createSpacer(8));


            // =================================================
            // PAYMENT INFORMATION
            // =================================================

            PdfPTable bankTable = new PdfPTable(2);


            bankTable.setWidthPercentage(100);


            bankTable.setWidths(new float[]{60, 40});


            PdfPCell bankCell = sectionCell("PAYMENT INFORMATION");


            if (company.getBankName() != null && !company.getBankName().isBlank()) {

                bankCell.addElement(new Paragraph("Bank Name: " + company.getBankName().trim(), normal));
            }


            if (company.getAccountHolderName() != null && !company.getAccountHolderName().isBlank()) {

                bankCell.addElement(new Paragraph("Account Name: " + company.getAccountHolderName().trim(), normal));
            }


            if (company.getAccountNumber() != null && !company.getAccountNumber().isBlank()) {

                bankCell.addElement(new Paragraph("Account Number: " + company.getAccountNumber().trim(), normal));
            }


            if (company.getIfscCode() != null && !company.getIfscCode().isBlank()) {

                bankCell.addElement(new Paragraph("IFSC: " + company.getIfscCode().trim(), normal));
            }


            // =================================================
            // TERMS
            // =================================================

            PdfPCell termsCell = sectionCell("TERMS & CONDITIONS");


            termsCell.addElement(new Paragraph("• Payment due within agreed terms.", normal));


            termsCell.addElement(new Paragraph("• Please retain this invoice.", normal));


            termsCell.addElement(new Paragraph("• Thank you for your business.", normal));


            bankTable.addCell(bankCell);

            bankTable.addCell(termsCell);


            document.add(bankTable);


            // =================================================
            // CLOSE
            // =================================================

            document.close();


            return output.toByteArray();


        } catch (Exception e) {

            throw new RuntimeException("Error while generating invoice PDF", e);
        }
    }


    // =========================================================
    // PAYMENT QR
    // =========================================================

    private Image generatePaymentQr(Company company, BigDecimal balance) throws Exception {

        String upi = company.getUpiId();


        if (upi == null || upi.isBlank()) {

            throw new IllegalArgumentException("UPI ID is not configured.");
        }


        String name = company.getName() != null && !company.getName().isBlank() ? company.getName().trim() : "Company";


        BigDecimal amount = balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;


        String qrData = "upi://pay" + "?pa=" + URLEncoder.encode(upi.trim(), StandardCharsets.UTF_8) + "&pn=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&am=" + amount.toPlainString() + "&cu=INR";


        BitMatrix matrix = new QRCodeWriter().encode(qrData, BarcodeFormat.QR_CODE, 250, 250);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();


        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);


        return Image.getInstance(stream.toByteArray());
    }


    // =========================================================
    // SUMMARY ROW
    // =========================================================

    private void summaryRow(PdfPTable table, String label, String value, boolean highlight, Color color) {

        Font labelFont = new Font(Font.HELVETICA, 8, Font.BOLD, color);


        Font valueFont = new Font(Font.HELVETICA, 8, Font.BOLD, color);


        PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));


        PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));


        labelCell.setPadding(6);

        valueCell.setPadding(6);


        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);


        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);


        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        labelCell.setBorderColor(BORDER);


        valueCell.setBorderColor(BORDER);


        if (highlight) {

            labelCell.setBackgroundColor(LIGHT_BLUE);

            valueCell.setBackgroundColor(LIGHT_BLUE);

        } else {

            labelCell.setBackgroundColor(WHITE);

            valueCell.setBackgroundColor(WHITE);
        }


        table.addCell(labelCell);

        table.addCell(valueCell);
    }


    // =========================================================
    // SECTION CELL
    // =========================================================

    private PdfPCell sectionCell(String title) {

        PdfPCell cell = new PdfPCell();


        cell.setPadding(8);

        cell.setBackgroundColor(LIGHT_GRAY);

        cell.setBorderColor(BORDER);


        Font titleFont = new Font(Font.HELVETICA, 8, Font.BOLD, PRIMARY);


        Paragraph titleParagraph = new Paragraph(title, titleFont);


        titleParagraph.setSpacingAfter(4);


        cell.addElement(titleParagraph);


        return cell;
    }


    // =========================================================
    // HEADER CELL
    // =========================================================

    private void addHeader(PdfPTable table, String text, Font font) {

        PdfPCell cell = new PdfPCell(new Paragraph(text, font));


        cell.setBackgroundColor(PRIMARY);


        cell.setHorizontalAlignment(Element.ALIGN_CENTER);


        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        cell.setPadding(7);


        cell.setBorderColor(PRIMARY_DARK);


        cell.setBorderWidth(0.7f);


        table.addCell(cell);
    }


    // =========================================================
    // ITEM CELL
    // =========================================================

    private void addItem(PdfPTable table, String text, int alignment) {

        Font font = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK);


        PdfPCell cell = new PdfPCell(new Paragraph(text != null ? text : "", font));


        cell.setPadding(5);


        cell.setHorizontalAlignment(alignment);


        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        cell.setBorderColor(BORDER);


        cell.setBorderWidth(0.6f);


        table.addCell(cell);
    }


    // =========================================================
    // SPACER
    // =========================================================

    private Paragraph createSpacer(float spacing) {

        Paragraph spacer = new Paragraph(" ");

        spacer.setSpacingAfter(spacing);

        return spacer;
    }


    // =========================================================
    // CURRENCY
    // =========================================================

    private String currency(BigDecimal amount) {

        if (amount == null) {

            return "₹ 0.00";
        }


        return "₹ " + String.format("%,.2f", amount.setScale(2, RoundingMode.HALF_UP));
    }


    // =========================================================
    // DATE
    // =========================================================

    private String formatDate(LocalDate date) {

        if (date == null) {

            return "-";
        }


        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }


    // =========================================================
    // CUSTOMER NAME
    // =========================================================

    private String buildCustomerName(String first, String last) {

        StringBuilder name = new StringBuilder();


        if (first != null && !first.isBlank()) {

            name.append(first.trim());
        }


        if (last != null && !last.isBlank()) {

            if (!name.isEmpty()) {

                name.append(" ");
            }


            name.append(last.trim());
        }


        return name.isEmpty() ? "Customer" : name.toString();
    }


    // =========================================================
    // STATUS
    // =========================================================

    private String getPaymentStatus(Invoice invoice) {

        try {

            if (invoice.getStatus() != null) {

                return invoice.getStatus().toString().toUpperCase();
            }

        } catch (Exception ignored) {
        }


        return "PENDING";
    }


    // =========================================================
    // AMOUNT IN WORDS
    // =========================================================

    private String amountInWords(BigDecimal amount) {

        if (amount == null) {

            return "Zero Rupees Only";
        }


        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);


        long rupees = rounded.longValue();


        int paise = rounded.remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();


        String result = convertNumberToWords(rupees) + " Rupees";


        if (paise > 0) {

            result += " and " + convertNumberToWords(paise) + " Paise";
        }


        return result + " Only";
    }


    // =========================================================
    // NUMBER TO WORDS
    // =========================================================

    private String convertNumberToWords(long number) {

        if (number == 0) {

            return "Zero";
        }


        if (number < 0) {

            return "Minus " + convertNumberToWords(-number);
        }


        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};


        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};


        if (number < 20) {

            return units[(int) number];
        }


        if (number < 100) {

            return tens[(int) number / 10] + (number % 10 != 0 ? " " + units[(int) number % 10] : "");
        }


        if (number < 1000) {

            return units[(int) number / 100] + " Hundred" + (number % 100 != 0 ? " " + convertNumberToWords(number % 100) : "");
        }


        if (number < 100000) {

            return convertNumberToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + convertNumberToWords(number % 1000) : "");
        }


        if (number < 10000000) {

            return convertNumberToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + convertNumberToWords(number % 100000) : "");
        }


        return convertNumberToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + convertNumberToWords(number % 10000000) : "");
    }


    // =========================================================
    // HEADER / FOOTER
    // =========================================================

    private static class InvoicePageEvent extends PdfPageEventHelper {

        private final Company company;


        public InvoicePageEvent(Company company) {

            this.company = company;
        }


        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte canvas = writer.getDirectContent();


            float left = document.left();


            float right = document.right();


            float top = document.top();


            float bottom = document.bottom();


            // =================================================
            // HEADER LINE
            // =================================================

            canvas.setColorStroke(PRIMARY);


            canvas.setLineWidth(2);


            canvas.moveTo(left, top + 27);


            canvas.lineTo(right, top + 27);


            canvas.stroke();


            // =================================================
            // COMPANY NAME
            // =================================================

            String companyName = company.getName() != null && !company.getName().isBlank() ? company.getName().trim() : "Company";


            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph(companyName, new Font(Font.HELVETICA, 16, Font.BOLD, PRIMARY)), left, top + 35, 0);


            // =================================================
            // INVOICE TITLE
            // =================================================

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Paragraph("INVOICE", new Font(Font.HELVETICA, 20, Font.BOLD, PRIMARY)), right, top + 31, 0);


            // =================================================
            // COMPANY DETAILS
            // =================================================

            String details = companyDetails();


            if (!details.isBlank()) {

                ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph(details, new Font(Font.HELVETICA, 7, Font.NORMAL, DARK)), left, top + 12, 0);
            }


            // =================================================
            // FOOTER LINE
            // =================================================

            canvas.setColorStroke(PRIMARY);


            canvas.setLineWidth(1);


            canvas.moveTo(left, bottom - 18);


            canvas.lineTo(right, bottom - 18);


            canvas.stroke();


            // =================================================
            // FOOTER LEFT
            // =================================================

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Paragraph("Thank you for choosing " + companyName, new Font(Font.HELVETICA, 7, Font.BOLD, PRIMARY)), left, bottom - 30, 0);


            // =================================================
            // FOOTER RIGHT
            // =================================================

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Paragraph("Page " + writer.getPageNumber(), new Font(Font.HELVETICA, 7, Font.NORMAL, DARK)), right, bottom - 30, 0);
        }


        // =====================================================
        // COMPANY DETAILS
        // =====================================================

        private String companyDetails() {

            StringBuilder text = new StringBuilder();


            if (company.getAddress() != null && !company.getAddress().isBlank()) {

                text.append(company.getAddress().trim());
            }


            if (company.getPhone() != null && !company.getPhone().isBlank()) {

                appendSeparator(text);

                text.append("Phone: " + company.getPhone().trim());
            }


            if (company.getEmail() != null && !company.getEmail().isBlank()) {

                appendSeparator(text);

                text.append("Email: " + company.getEmail().trim());
            }


            if (company.getGstNumber() != null && !company.getGstNumber().isBlank()) {

                appendSeparator(text);

                text.append("GSTIN: " + company.getGstNumber().trim());
            }


            return text.toString();
        }


        // =====================================================
        // SEPARATOR
        // =====================================================

        private void appendSeparator(StringBuilder text) {

            if (!text.isEmpty()) {

                text.append("  |  ");
            }
        }
    }
}
