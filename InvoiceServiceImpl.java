package com.main.springboot.services;

import com.main.springboot.model.Invoice;
import com.main.springboot.model.InvoiceItem;
import com.main.springboot.model.Product;
import com.main.springboot.repository.InvoiceRepository;
import com.main.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	
	
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public void saveInvoice(Invoice invoice) {
        for (InvoiceItem item : invoice.getItems()) {
            Long productId = item.getProduct().getId();
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                int currentStock = product.getQuantityInStock();
                int soldQty = item.getQuantity();
                product.setQuantityInStock(Math.max(0, currentStock - soldQty));
                productRepository.save(product);

                item.setProduct(product);
                item.setSubtotal(item.getPrice() * item.getQuantity());
                item.setInvoice(invoice);
            }
        }

        // Optional GST logic:
        double total = invoice.getItems().stream()
                .mapToDouble(InvoiceItem::getSubtotal)
                .sum();

        double gst = total * 0.18; // 18% GST example
        invoice.setGstAmount(gst);
        invoice.setTotalAmount(total + gst);

        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    
    @Override
    public Double getTotalSales() {
        Double total = invoiceRepository.getTotalSales();
        return total != null ? total : 0.0;
    }

    @Override
    public Double getTotalGSTCollected() {
        Double totalGST = invoiceRepository.getTotalGSTCollected();
        return totalGST != null ? totalGST : 0.0;
    }

    @Override
    public Double getNetSales() {
        return getTotalSales() - getTotalGSTCollected();
    }
    
}
