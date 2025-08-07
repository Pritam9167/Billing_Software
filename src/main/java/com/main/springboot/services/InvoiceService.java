
package com.main.springboot.services;

import com.main.springboot.model.Invoice;
import java.util.List;

public interface InvoiceService {
	
	Double getTotalSales();
	Double getTotalGSTCollected();
	Double getNetSales();

    void saveInvoice(Invoice invoice);
    List<Invoice> getAllInvoices();
}
