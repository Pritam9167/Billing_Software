package com.main.springboot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.springboot.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p")
    Double getTotalPurchases();
    
    List<Purchase> findByPurchaseDate(LocalDate date);
    List<Purchase> findByPurchaseDateBetween(LocalDate start, LocalDate end);

    
}
