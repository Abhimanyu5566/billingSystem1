package com.project.billingManagementSystem.service;


import com.project.billingManagementSystem.entity.invoice.Items;

import java.util.List;

public interface InvoiceItemsService {

    Items addItems(String invoiceNo, Items items);

    Items updateInvoiceItems(String invoiceNo, Integer serialNo, Items updatedItem);

    List<Items> addItems(String invoiceNo, List<Items> items);

    Items deleteInvoiceItem(String invoiceNo, Integer serialNo);





}
