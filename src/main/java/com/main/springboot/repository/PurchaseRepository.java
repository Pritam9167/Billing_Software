package com.main.springboot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.main.springboot.model.Purchase;
import com.main.springboot.model.User;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.createdBy = :user")
    Double getTotalPurchases(@Param("user") User user);

    List<Purchase> findByCreatedBy(User user);

    List<Purchase> findByPurchaseDateAndCreatedBy(LocalDate date, User user);

    List<Purchase> findByPurchaseDateBetweenAndCreatedBy(LocalDate start, LocalDate end, User user);
    
  
    
}
