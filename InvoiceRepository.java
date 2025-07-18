package com.main.springboot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.springboot.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // ✅ This will correctly sum the totalAmount from all invoices
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i")
    Double getTotalSales();

    // ✅ Optional: Get total GST collected
    @Query("SELECT SUM(i.gstAmount) FROM Invoice i")
    Double getTotalGST();

    // ✅ Optional: Count of all invoices
    @Query("SELECT COUNT(i) FROM Invoice i")
    Long getTotalInvoices();
    
    
    

    @Query("SELECT SUM(i.gstAmount) FROM Invoice i")
    Double getTotalGSTCollected();

    
    List<Invoice> findByDate(LocalDate date);
    List<Invoice> findByDateBetween(LocalDate start, LocalDate end);

	List<Invoice> findByCustomerName(String customerName);

}
