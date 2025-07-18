package com.main.springboot.services;

import com.main.springboot.model.Product;
import com.main.springboot.model.Purchase;
import com.main.springboot.model.PurchaseItem;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public void savePurchase(Purchase purchase) {
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
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Purchase not found with ID: " + id));
    }

    @Override
    public List<Purchase> getPurchasesByDate(LocalDate date) {
        return purchaseRepository.findByPurchaseDate(date);
    }

    @Override
    public List<Purchase> getPurchasesBetweenDates(LocalDate start, LocalDate end) {
        return purchaseRepository.findByPurchaseDateBetween(start, end);
    }

    @Override
    public Double getTotalPurchases() {
        return purchaseRepository.getTotalPurchases();
    }
}
