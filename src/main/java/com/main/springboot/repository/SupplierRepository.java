package com.main.springboot.repository;

import com.main.springboot.model.Supplier;
import com.main.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByCreatedBy(User user);
}
