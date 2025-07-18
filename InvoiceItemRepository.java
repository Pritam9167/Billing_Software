package com.main.springboot.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.main.springboot.model.InvoiceItem;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByProduct_Name(String productName);
    List<InvoiceItem> findByInvoice_CustomerName(String customerName); // if you store customerName in Invoice
}
