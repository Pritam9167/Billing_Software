
package com.main.springboot.repository;

import com.main.springboot.model.Product;
import com.main.springboot.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCreatedBy(User user);

    @Query("SELECT p.name, SUM(i.quantity) as qty " +
           "FROM InvoiceItem i JOIN i.product p " +
           "WHERE p.createdBy = :user " +
           "GROUP BY p.name ORDER BY qty DESC")
    List<Object[]> findTop5Products(@Param("user") User user, Pageable pageable);

    List<Product> findByQuantityInStockLessThanEqualAndCreatedBy(int threshold, User user);
    
    
    long countByCreatedBy(User user);

    @Query("SELECT p.name, SUM(i.quantity) FROM InvoiceItem i JOIN i.product p " +
           "WHERE p.createdBy = :user GROUP BY p.name ORDER BY SUM(i.quantity) DESC")
    List<Object[]> findTop5ProductsByUser(@Param("user") User user, Pageable pageable);

    List<Product> findByCreatedByAndQuantityInStockLessThanEqual(User user, int threshold);

}
