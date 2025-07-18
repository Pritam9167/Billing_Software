package com.main.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.springboot.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT i.customerName, SUM(i.totalAmount) FROM Invoice i GROUP BY i.customerName ORDER BY SUM(i.totalAmount) DESC")
    List<Object[]> findTop5Customers(Pageable pageable);
}
