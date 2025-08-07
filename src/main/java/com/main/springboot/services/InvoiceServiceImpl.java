package com.main.springboot.services;

import com.main.springboot.model.Invoice;
import com.main.springboot.model.InvoiceItem;
import com.main.springboot.model.Product;
import com.main.springboot.model.User;
import com.main.springboot.repository.InvoiceRepository;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Utility to get logged-in user
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        return userRepository.findByUsername(username);
    }

    @Override
    public void saveInvoice(Invoice invoice) {
        // Attach logged-in user
        invoice.setCreatedBy(getLoggedInUser());

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

        // GST logic
        double total = invoice.getItems().stream()
                .mapToDouble(InvoiceItem::getSubtotal)
                .sum();

        double gst = total * 0.18; // 18% GST
        invoice.setGstAmount(gst);
        invoice.setTotalAmount(total + gst);

        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        // ✅ Fetch only invoices created by logged-in user
        User user = getLoggedInUser();
        return invoiceRepository.findByCreatedBy(user);
    }

    @Override
    public Double getTotalSales() {
        User user = getLoggedInUser();
        Double total = invoiceRepository.getTotalSalesByUser(user);
        return total != null ? total : 0.0;
    }

    @Override
    public Double getTotalGSTCollected() {
        User user = getLoggedInUser();
        Double totalGST = invoiceRepository.getTotalGSTByUser(user);
        return totalGST != null ? totalGST : 0.0;
    }

    @Override
    public Double getNetSales() {
        return getTotalSales() - getTotalGSTCollected();
    }
}
