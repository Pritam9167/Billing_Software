package com.main.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.main.springboot.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
