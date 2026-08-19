//package com.project.billingManagementSystem.service.impl;
//
//import com.project.billingManagementSystem.entity.invoice.Invoice;
//import com.project.billingManagementSystem.entity.invoice.Items;
//import com.project.billingManagementSystem.repository.InvoiceItemsRepository;
//import com.project.billingManagementSystem.repository.InvoiceRepository;
//import com.project.billingManagementSystem.service.InvoiceItemsService;
//import com.project.billingManagementSystem.service.InvoiceService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Component
//public class InvoiceItemsServiceImpl implements InvoiceItemsService {
//
//    @Autowired
//    private InvoiceItemsRepository repository;
//
//    @Autowired
//    private InvoiceService invoiceService;
//
//    @Autowired
//    private InvoiceRepository invoiceRepository;
//
//
//    // =========================================================
//    // ADD SINGLE ITEM
//    // =========================================================
//
//    @Override
//    @Transactional
//    public Items addItems(String invoiceNo, Items items) {
//
//        // Find invoice
//        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);
//
//        if (invoice == null) {
//            throw new RuntimeException("Invoice not found with invoiceNo: " + invoiceNo);
//        }
//
//        // Validate quantity
//        if (items.getQuantity() == null || items.getQuantity() <= 0) {
//
//            throw new RuntimeException("Quantity must be greater than 0");
//        }
//
//        // Validate rate
//        if (items.getRate() == null || items.getRate().compareTo(BigDecimal.ZERO) < 0) {
//
//            throw new RuntimeException("Rate cannot be null or negative");
//        }
//
//        // =====================================================
//        // AUTO GENERATE ITEM SERIAL NUMBER
//        // =====================================================
//
//        Integer lastSerialNo = repository.findMaxItemSerialNoByInvoiceId(invoice.getInvoiceId());
//
//        int nextSerialNo = lastSerialNo == null ? 1 : lastSerialNo + 1;
//
//        items.setItemSerialNo(nextSerialNo);
//
//        // =====================================================
//        // CALCULATE ITEM AMOUNT
//        // =====================================================
//
//        BigDecimal amount = items.getRate().multiply(BigDecimal.valueOf(items.getQuantity()));
//
//        items.setAmount(amount);
//
//        // Associate item with invoice
//        items.setInvoice(invoice);
//
//        // Save item
//        Items savedItem = repository.save(items);
//
//        // =====================================================
//        // UPDATE INVOICE TOTAL
//        // =====================================================
//
//        BigDecimal currentTotal = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
//
//        invoice.setTotalAmount(currentTotal.add(amount));
//
//        // Save invoice
//        invoiceRepository.save(invoice);
//
//        return savedItem;
//    }
//
//
//    // =========================================================
//    // UPDATE ITEM
//    // invoiceNo + serialNo
//    // =========================================================
//
//    @Override
//    @Transactional
//    public Items updateInvoiceItems(String invoiceNo, Integer serialNo, Items updatedItem) {
//
//        // Find invoice
//        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);
//
//        if (invoice == null) {
//            throw new RuntimeException("Invoice not found with invoiceNo: " + invoiceNo);
//        }
//        // Find existing item
//        Items existingItem = repository.findByInvoice_InvoiceIdAndItemSerialNo(invoice.getInvoiceId(), serialNo).orElseThrow(() -> new RuntimeException("Item not found with serialNo: " + serialNo + " for invoiceNo: " + invoiceNo));
//
//        // Store old amount
//        BigDecimal oldAmount = existingItem.getAmount() != null ? existingItem.getAmount() : BigDecimal.ZERO;
//
//
//        // =====================================================
//        // UPDATE DESCRIPTION
//        // =====================================================
//
//        if (updatedItem.getDescription() != null && !updatedItem.getDescription().trim().isEmpty()) {
//
//            existingItem.setDescription(updatedItem.getDescription().trim());
//        }
//
//
//        // =====================================================
//        // UPDATE QUANTITY
//        // =====================================================
//
//        if (updatedItem.getQuantity() != null) {
//
//            if (updatedItem.getQuantity() <= 0) {
//                throw new IllegalArgumentException("Quantity must be greater than 0");
//            }
//
//            existingItem.setQuantity(updatedItem.getQuantity());
//        }
//
//
//        // =====================================================
//        // UPDATE RATE
//        // =====================================================
//
//        if (updatedItem.getRate() != null) {
//
//            if (updatedItem.getRate().compareTo(BigDecimal.ZERO) < 0) {
//
//                throw new IllegalArgumentException("Rate cannot be negative");
//            }
//
//            existingItem.setRate(updatedItem.getRate());
//        }
//
//
//        // =====================================================
//        // GET FINAL VALUES
//        // =====================================================
//
//        Integer quantity = existingItem.getQuantity();
//
//        BigDecimal rate = existingItem.getRate();
//
//        if (quantity == null || quantity <= 0) {
//
//            throw new IllegalArgumentException("Quantity must be greater than 0");
//        }
//
//        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0) {
//
//            throw new IllegalArgumentException("Rate cannot be null or negative");
//        }
//
//
//        // =====================================================
//        // CALCULATE NEW AMOUNT
//        // =====================================================
//
//        BigDecimal newAmount = rate.multiply(BigDecimal.valueOf(quantity));
//
//        existingItem.setAmount(newAmount);
//
//        // Keep invoice relationship
//        existingItem.setInvoice(invoice);
//
//
//        // =====================================================
//        // UPDATE INVOICE TOTAL
//        // =====================================================
//
//        BigDecimal difference = newAmount.subtract(oldAmount);
//
//        BigDecimal currentTotal = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
//
//        invoice.setTotalAmount(currentTotal.add(difference));
//
//
//        // Save item
//        Items savedItem = repository.save(existingItem);
//
//        // Save invoice
//        invoiceRepository.save(invoice);
//
//        return savedItem;
//    }
//
//
//    // =========================================================
//    // ADD MULTIPLE ITEMS
//    // =========================================================
//
//    @Override
//    @Transactional
//    public List<Items> addItems(String invoiceNo, List<Items> items) {
//
//        // Find invoice
//        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);
//
//        if (invoice == null) {
//            throw new RuntimeException("Invoice not found with invoiceNo: " + invoiceNo);
//        }
//
//        // Validate list
//        if (items == null || items.isEmpty()) {
//            throw new RuntimeException("Item list cannot be empty");
//        }
//
//
//        // =====================================================
//        // FIND LAST SERIAL NUMBER
//        // =====================================================
//
//        Integer lastSerialNo = repository.findMaxItemSerialNoByInvoiceId(invoice.getInvoiceId());
//
//        int nextSerialNo = lastSerialNo == null ? 1 : lastSerialNo + 1;
//
//
//        BigDecimal totalItemAmount = BigDecimal.ZERO;
//
//
//        // =====================================================
//        // PROCESS ALL ITEMS
//        // =====================================================
//
//        for (Items item : items) {
//
//            // Validate quantity
//            if (item.getQuantity() == null || item.getQuantity() <= 0) {
//
//                throw new RuntimeException("Quantity must be greater than 0");
//            }
//
//            // Validate rate
//            if (item.getRate() == null || item.getRate().compareTo(BigDecimal.ZERO) < 0) {
//
//                throw new RuntimeException("Rate cannot be null or negative");
//            }
//
//
//            // =================================================
//            // AUTO GENERATE SERIAL NUMBER
//            // =================================================
//
//            item.setItemSerialNo(nextSerialNo++);
//
//
//            // =================================================
//            // CALCULATE AMOUNT
//            // =================================================
//
//            BigDecimal amount = item.getRate().multiply(BigDecimal.valueOf(item.getQuantity()));
//
//            item.setAmount(amount);
//
//
//            // =================================================
//            // ASSOCIATE WITH INVOICE
//            // =================================================
//
//            item.setInvoice(invoice);
//
//
//            // =================================================
//            // ADD TO TOTAL
//            // =================================================
//
//            totalItemAmount = totalItemAmount.add(amount);
//        }
//
//
//        // =====================================================
//        // SAVE ALL ITEMS
//        // =====================================================
//
//        List<Items> savedItems = repository.saveAll(items);
//
//
//        // =====================================================
//        // UPDATE INVOICE TOTAL
//        // =====================================================
//
//        BigDecimal currentTotal = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
//
//        invoice.setTotalAmount(currentTotal.add(totalItemAmount));
//
//
//        // Save invoice
//        invoiceRepository.save(invoice);
//
//
//        return savedItems;
//    }
//
//    @Override
//    @Transactional
//    public Items deleteInvoiceItem(String invoiceNo, Integer serialNo) {
//
//        // Find invoice
//        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);
//
//        if (invoice == null) {
//            throw new RuntimeException(
//                    "Invoice not found with invoiceNo: " + invoiceNo
//            );
//        }
//
//        // Validate serial number
//        if (serialNo == null || serialNo <= 0) {
//            throw new IllegalArgumentException(
//                    "Valid Item Serial No. is required"
//            );
//        }
//
//        // Find item
//        Items existingItem = repository
//                .findByInvoice_InvoiceIdAndItemSerialNo(
//                        invoice.getInvoiceId(),
//                        serialNo
//                )
//                .orElseThrow(() -> new RuntimeException(
//                        "Item not found with serialNo: "
//                                + serialNo
//                                + " for invoiceNo: "
//                                + invoiceNo
//                ));
//
//        // Get item amount
//        BigDecimal itemAmount = existingItem.getAmount() != null
//                ? existingItem.getAmount()
//                : BigDecimal.ZERO;
//
//        // Delete item
//        repository.delete(existingItem);
//
//        // Update invoice total
//        BigDecimal currentTotal = invoice.getTotalAmount() != null
//                ? invoice.getTotalAmount()
//                : BigDecimal.ZERO;
//
//        BigDecimal newTotal = currentTotal.subtract(itemAmount);
//
//        // Prevent negative invoice total
//        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
//            newTotal = BigDecimal.ZERO;
//        }
//
//        invoice.setTotalAmount(newTotal);
//
//        // Save invoice
//        invoiceRepository.save(invoice);
//
//        return existingItem;
//    }
//}
//

package com.project.billingManagementSystem.service.impl;

import com.project.billingManagementSystem.entity.invoice.Invoice;
import com.project.billingManagementSystem.entity.invoice.Items;
import com.project.billingManagementSystem.repository.InvoiceItemsRepository;
import com.project.billingManagementSystem.repository.InvoiceRepository;
import com.project.billingManagementSystem.service.InvoiceItemsService;
import com.project.billingManagementSystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InvoiceItemsServiceImpl implements InvoiceItemsService {

    @Autowired
    private InvoiceItemsRepository repository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;


    // =========================================================
    // ADD SINGLE ITEM
    // =========================================================

    @Override
    @Transactional
    public Items addItems(String invoiceNo, Items items) {

        // Find invoice
        Invoice invoice = invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {
            throw new RuntimeException(
                    "Invoice not found with invoiceNo: " + invoiceNo
            );
        }

        // =====================================================
        // DEFAULT QUANTITY TO 1 IF NOT PROVIDED
        // =====================================================

        if (items.getQuantity() == null) {
            items.setQuantity(1);
        }

        // Validate quantity
        if (items.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }

        // =====================================================
        // VALIDATE RATE
        // =====================================================

        if (items.getRate() == null
                || items.getRate().compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Rate cannot be null or negative"
            );
        }

        // =====================================================
        // AUTO GENERATE ITEM SERIAL NUMBER
        // =====================================================

        Integer lastSerialNo =
                repository.findMaxItemSerialNoByInvoiceId(
                        invoice.getInvoiceId()
                );

        int nextSerialNo =
                lastSerialNo == null ? 1 : lastSerialNo + 1;

        items.setItemSerialNo(nextSerialNo);

        // =====================================================
        // CALCULATE ITEM AMOUNT
        // =====================================================

        BigDecimal amount = items.getRate()
                .multiply(BigDecimal.valueOf(items.getQuantity()));

        items.setAmount(amount);

        // Associate item with invoice
        items.setInvoice(invoice);

        // Save item
        Items savedItem = repository.save(items);

        // =====================================================
        // UPDATE INVOICE TOTAL
        // =====================================================

        BigDecimal currentTotal =
                invoice.getTotalAmount() == null
                        ? BigDecimal.ZERO
                        : invoice.getTotalAmount();

        invoice.setTotalAmount(
                currentTotal.add(amount)
        );

        // Save invoice
        invoiceRepository.save(invoice);

        return savedItem;
    }


    // =========================================================
    // UPDATE ITEM
    // invoiceNo + serialNo
    // =========================================================

    @Override
    @Transactional
    public Items updateInvoiceItems(
            String invoiceNo,
            Integer serialNo,
            Items updatedItem) {

        // Find invoice
        Invoice invoice =
                invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {
            throw new RuntimeException(
                    "Invoice not found with invoiceNo: " + invoiceNo
            );
        }

        // =====================================================
        // FIND EXISTING ITEM
        // =====================================================

        Items existingItem =
                repository
                        .findByInvoice_InvoiceIdAndItemSerialNo(
                                invoice.getInvoiceId(),
                                serialNo
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item not found with serialNo: "
                                                + serialNo
                                                + " for invoiceNo: "
                                                + invoiceNo
                                )
                        );

        // =====================================================
        // STORE OLD AMOUNT
        // =====================================================

        BigDecimal oldAmount =
                existingItem.getAmount() != null
                        ? existingItem.getAmount()
                        : BigDecimal.ZERO;


        // =====================================================
        // UPDATE DESCRIPTION
        // =====================================================

        if (updatedItem.getDescription() != null
                && !updatedItem.getDescription().trim().isEmpty()) {

            existingItem.setDescription(
                    updatedItem.getDescription().trim()
            );
        }


        // =====================================================
        // UPDATE QUANTITY
        // =====================================================

        if (updatedItem.getQuantity() != null) {

            if (updatedItem.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than 0"
                );
            }

            existingItem.setQuantity(
                    updatedItem.getQuantity()
            );
        }

        // If quantity is not provided during UPDATE,
        // existing quantity will remain unchanged.


        // =====================================================
        // UPDATE RATE
        // =====================================================

        if (updatedItem.getRate() != null) {

            if (updatedItem.getRate()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalArgumentException(
                        "Rate cannot be negative"
                );
            }

            existingItem.setRate(
                    updatedItem.getRate()
            );
        }


        // =====================================================
        // GET FINAL VALUES
        // =====================================================

        Integer quantity =
                existingItem.getQuantity();

        BigDecimal rate =
                existingItem.getRate();


        // Safety fallback:
        // If old database record has NULL quantity,
        // use default quantity = 1.

        if (quantity == null) {
            quantity = 1;
            existingItem.setQuantity(quantity);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        if (rate == null
                || rate.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Rate cannot be null or negative"
            );
        }


        // =====================================================
        // CALCULATE NEW AMOUNT
        // =====================================================

        BigDecimal newAmount =
                rate.multiply(
                        BigDecimal.valueOf(quantity)
                );

        existingItem.setAmount(newAmount);

        // Keep invoice relationship
        existingItem.setInvoice(invoice);


        // =====================================================
        // UPDATE INVOICE TOTAL
        // =====================================================

        BigDecimal difference =
                newAmount.subtract(oldAmount);

        BigDecimal currentTotal =
                invoice.getTotalAmount() != null
                        ? invoice.getTotalAmount()
                        : BigDecimal.ZERO;

        invoice.setTotalAmount(
                currentTotal.add(difference)
        );


        // =====================================================
        // SAVE ITEM
        // =====================================================

        Items savedItem =
                repository.save(existingItem);

        // Save invoice
        invoiceRepository.save(invoice);

        return savedItem;
    }


    // =========================================================
    // ADD MULTIPLE ITEMS
    // =========================================================

    @Override
    @Transactional
    public List<Items> addItems(
            String invoiceNo,
            List<Items> items) {

        // Find invoice
        Invoice invoice =
                invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {
            throw new RuntimeException(
                    "Invoice not found with invoiceNo: " + invoiceNo
            );
        }

        // =====================================================
        // VALIDATE LIST
        // =====================================================

        if (items == null || items.isEmpty()) {
            throw new RuntimeException(
                    "Item list cannot be empty"
            );
        }


        // =====================================================
        // FIND LAST SERIAL NUMBER
        // =====================================================

        Integer lastSerialNo =
                repository.findMaxItemSerialNoByInvoiceId(
                        invoice.getInvoiceId()
                );

        int nextSerialNo =
                lastSerialNo == null
                        ? 1
                        : lastSerialNo + 1;


        BigDecimal totalItemAmount =
                BigDecimal.ZERO;


        // =====================================================
        // PROCESS ALL ITEMS
        // =====================================================

        for (Items item : items) {

            // =================================================
            // DEFAULT QUANTITY TO 1
            // =================================================

            if (item.getQuantity() == null) {
                item.setQuantity(1);
            }

            // =================================================
            // VALIDATE QUANTITY
            // =================================================

            if (item.getQuantity() <= 0) {
                throw new RuntimeException(
                        "Quantity must be greater than 0"
                );
            }

            // =================================================
            // VALIDATE RATE
            // =================================================

            if (item.getRate() == null
                    || item.getRate()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw new RuntimeException(
                        "Rate cannot be null or negative"
                );
            }


            // =================================================
            // AUTO GENERATE SERIAL NUMBER
            // =================================================

            item.setItemSerialNo(
                    nextSerialNo++
            );


            // =================================================
            // CALCULATE AMOUNT
            // =================================================

            BigDecimal amount =
                    item.getRate()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            item.setAmount(amount);


            // =================================================
            // ASSOCIATE WITH INVOICE
            // =================================================

            item.setInvoice(invoice);


            // =================================================
            // ADD TO TOTAL
            // =================================================

            totalItemAmount =
                    totalItemAmount.add(amount);
        }


        // =====================================================
        // SAVE ALL ITEMS
        // =====================================================

        List<Items> savedItems =
                repository.saveAll(items);


        // =====================================================
        // UPDATE INVOICE TOTAL
        // =====================================================

        BigDecimal currentTotal =
                invoice.getTotalAmount() == null
                        ? BigDecimal.ZERO
                        : invoice.getTotalAmount();

        invoice.setTotalAmount(
                currentTotal.add(totalItemAmount)
        );


        // Save invoice
        invoiceRepository.save(invoice);


        return savedItems;
    }


    // =========================================================
    // DELETE ITEM
    // =========================================================

    @Override
    @Transactional
    public Items deleteInvoiceItem(
            String invoiceNo,
            Integer serialNo) {

        // Find invoice
        Invoice invoice =
                invoiceService.findInvoiceNO(invoiceNo);

        if (invoice == null) {
            throw new RuntimeException(
                    "Invoice not found with invoiceNo: "
                            + invoiceNo
            );
        }

        // =====================================================
        // VALIDATE SERIAL NUMBER
        // =====================================================

        if (serialNo == null || serialNo <= 0) {
            throw new IllegalArgumentException(
                    "Valid Item Serial No. is required"
            );
        }

        // =====================================================
        // FIND ITEM
        // =====================================================

        Items existingItem =
                repository
                        .findByInvoice_InvoiceIdAndItemSerialNo(
                                invoice.getInvoiceId(),
                                serialNo
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item not found with serialNo: "
                                                + serialNo
                                                + " for invoiceNo: "
                                                + invoiceNo
                                )
                        );

        // =====================================================
        // GET ITEM AMOUNT
        // =====================================================

        BigDecimal itemAmount =
                existingItem.getAmount() != null
                        ? existingItem.getAmount()
                        : BigDecimal.ZERO;

        // =====================================================
        // DELETE ITEM
        // =====================================================

        repository.delete(existingItem);

        // =====================================================
        // UPDATE INVOICE TOTAL
        // =====================================================

        BigDecimal currentTotal =
                invoice.getTotalAmount() != null
                        ? invoice.getTotalAmount()
                        : BigDecimal.ZERO;

        BigDecimal newTotal =
                currentTotal.subtract(itemAmount);

        // Prevent negative invoice total
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            newTotal = BigDecimal.ZERO;
        }

        invoice.setTotalAmount(newTotal);

        // Save invoice
        invoiceRepository.save(invoice);

        return existingItem;
    }
}
