package com.main.springboot.repository;

import com.main.springboot.model.StockConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockConfigRepository extends JpaRepository<StockConfig, Long> {
}
