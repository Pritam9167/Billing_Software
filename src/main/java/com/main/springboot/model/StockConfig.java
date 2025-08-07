package com.main.springboot.model;

import jakarta.persistence.*;

@Entity
public class StockConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private int lowStockThreshold = 5;  // default value

    // Getters & Setters
    public Long getId() { return id; }

    public int getLowStockThreshold() { return lowStockThreshold; }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
