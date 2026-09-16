package com.project.billingManagementSystem.mapperDTO;

import com.project.billingManagementSystem.entity.dto.createDTO.InvoiceRequest;
import com.project.billingManagementSystem.entity.dto.updateDTO.InvoiceUpdateRequest;
import com.project.billingManagementSystem.entity.invoice.Invoice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {


    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "balanceAmount", ignore = true)
    @Mapping(target = "advanceAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "items", ignore = true)
    Invoice toEntity(InvoiceRequest request);


    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "balanceAmount", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateEntity(InvoiceUpdateRequest request, @MappingTarget Invoice invoice);
}
