package com.main.springboot.services;

import com.main.springboot.model.Product;
import com.main.springboot.model.Purchase;
import com.main.springboot.model.PurchaseItem;
import com.main.springboot.model.User;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.PurchaseRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    @Override
    public void savePurchase(Purchase purchase) {
        User user = getLoggedInUser();
        purchase.setCreatedBy(user);

        double totalAmount = 0;

        for (PurchaseItem item : purchase.getItems()) {
            Long productId = item.getProduct().getId();
            Product product = productRepository.findById(productId).orElse(null);

            if (product != null) {
                // Update product stock
                int currentStock = product.getQuantityInStock();
                product.setQuantityInStock(currentStock + item.getQuantity());
                productRepository.save(product);

                // Calculate subtotal and set relationships
                item.setProduct(product);
                item.setSubtotal(item.getPrice() * item.getQuantity());
                item.setPurchase(purchase);

                totalAmount += item.getSubtotal();
            }
        }

        purchase.setTotalAmount(totalAmount);
        purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findByCreatedBy(getLoggedInUser());
    }

    @Override
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .filter(p -> p.getCreatedBy().equals(getLoggedInUser()))
                .orElseThrow(() -> new RuntimeException("❌ Purchase not found with ID: " + id));
    }

    @Override
    public List<Purchase> getPurchasesByDate(LocalDate date) {
        return purchaseRepository.findByPurchaseDateAndCreatedBy(date, getLoggedInUser());
    }

    @Override
    public List<Purchase> getPurchasesBetweenDates(LocalDate start, LocalDate end) {
        return purchaseRepository.findByPurchaseDateBetweenAndCreatedBy(start, end, getLoggedInUser());
    }

    @Override
    public Double getTotalPurchases() {
        return purchaseRepository.getTotalPurchases(getLoggedInUser());
    }
}
