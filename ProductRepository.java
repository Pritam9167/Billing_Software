package com.main.springboot.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.main.springboot.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.name, SUM(i.quantity) as qty " +
           "FROM InvoiceItem i JOIN i.product p " +
           "GROUP BY p.name ORDER BY qty DESC")
    List<Object[]> findTop5Products(Pageable pageable);
    
    List<Product> findByQuantityInStockLessThanEqual(int threshold);
    
}
