package com.project.billingManagementSystem.service;

import com.project.billingManagementSystem.entity.dto.createDTO.ItemsRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.ItemsUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Items;

import java.util.List;

public interface InvoiceItemsService {

    Items addItems(String invoiceNo, ItemsRequest request);

    List<Items> addItems(String invoiceNo, List<ItemsRequest> requests);

    Items updateInvoiceItems(String invoiceNo, Integer serialNo, ItemsUpdateRequest request);

    Items deleteInvoiceItem(String invoiceNo, Integer serialNo);
}


