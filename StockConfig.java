package com.main.springboot.model;

import jakarta.persistence.*;

@Entity
public class StockConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int lowStockThreshold = 5;  // default value

    // Getters & Setters
    public Long getId() { return id; }

    public int getLowStockThreshold() { return lowStockThreshold; }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
