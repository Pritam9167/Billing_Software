package com.main.springboot.repository;

import com.main.springboot.model.StockConfig;
import com.main.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockConfigRepository extends JpaRepository<StockConfig, Long> {
    Optional<StockConfig> findByCreatedBy(User user);
}
