package com.main.springboot.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.main.springboot.model.User;
import com.main.springboot.repository.CustomerRepository;
import com.main.springboot.repository.InvoiceRepository;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.PurchaseRepository;
import com.main.springboot.repository.UserRepository;

@Service
public class DashboardServices {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Utility method to get logged-in user
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    public double getTotalSales() {
        return Optional.ofNullable(invoiceRepository.getTotalSales(getLoggedInUser())).orElse(0.0);
    }

    public double getTotalPurchases() {
        return Optional.ofNullable(purchaseRepository.getTotalPurchases(getLoggedInUser())).orElse(0.0);
    }

    public long getTotalProducts() {
        return productRepository.countByCreatedBy(getLoggedInUser());
    }

    public long getTotalCustomers() {
        return customerRepository.countByCreatedBy(getLoggedInUser());
    }

    public long getTotalInvoices() {
        return invoiceRepository.countByCreatedBy(getLoggedInUser());
    }

    public double getTotalGST() {
        return Optional.ofNullable(invoiceRepository.getTotalGST(getLoggedInUser())).orElse(0.0);
    }

    public List<Object[]> getTop5Products() {
        return productRepository.findTop5ProductsByUser(getLoggedInUser(), PageRequest.of(0, 5));
    }

    public List<Object[]> getTop5Customers() {
        return customerRepository.findTop5CustomersByUser(getLoggedInUser(), PageRequest.of(0, 5));
    }
}
