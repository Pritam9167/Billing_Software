package com.main.springboot.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER"; // Can be USER or ADMIN

    // optional: One user can have many invoices
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    // optional: One user can have many purchases
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Purchase> purchases;
    
    
    @Column(unique = true, nullable = false)
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public List<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<Purchase> purchases) {
		this.purchases = purchases;
	}

    
}
