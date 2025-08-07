
package com.main.springboot.services;

import com.main.springboot.model.Purchase;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    void savePurchase(Purchase purchase);
    List<Purchase> getAllPurchases();
    Purchase getPurchaseById(Long id);
    List<Purchase> getPurchasesByDate(LocalDate date);
    List<Purchase> getPurchasesBetweenDates(LocalDate start, LocalDate end);
    Double getTotalPurchases();
}
