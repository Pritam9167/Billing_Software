
package com.main.springboot.repository;

import com.main.springboot.model.Customer;
import com.main.springboot.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCreatedBy(User user);

    @Query("SELECT i.customerName, SUM(i.totalAmount) " +
    	       "FROM Invoice i " +
    	       "WHERE i.createdBy = :user " +
    	       "GROUP BY i.customerName " +
    	       "ORDER BY SUM(i.totalAmount) DESC")
    	List<Object[]> findTop5Customers(@Param("user") User user, Pageable pageable);

    
    long countByCreatedBy(User user);

    @Query("SELECT i.customerName, SUM(i.totalAmount) FROM Invoice i " +
           "WHERE i.createdBy = :user GROUP BY i.customerName ORDER BY SUM(i.totalAmount) DESC")
    List<Object[]> findTop5CustomersByUser(@Param("user") User user, Pageable pageable);

}

