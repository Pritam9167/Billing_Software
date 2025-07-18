package com.main.springboot.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.main.springboot.repository.CustomerRepository;
import com.main.springboot.repository.InvoiceRepository;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.PurchaseRepository;

@Service
public class DashboardServices {

	@Autowired
	private InvoiceRepository invoiceRepository;

	public double getTotalSales() {
	    return Optional.ofNullable(invoiceRepository.getTotalSales()).orElse(0.0);
	}
	@Autowired
	private PurchaseRepository purchaseRepository;

	public double getTotalPurchases() {
	    return Optional.ofNullable(purchaseRepository.getTotalPurchases()).orElse(0.0);
	}
	@Autowired
	private ProductRepository productRepository;

	public long getTotalProducts() {
	    return productRepository.count();
	}
	@Autowired
	private CustomerRepository customerRepository;

	public long getTotalCustomers() {
	    return customerRepository.count();
	}
	public long getTotalInvoices() {
	    return invoiceRepository.count();
	}
	public double getTotalGST() {
	    return Optional.ofNullable(invoiceRepository.getTotalGST()).orElse(0.0);
	}
	
	public List<Object[]> getTop5Products() {
		return customerRepository.findTop5Customers(PageRequest.of(0, 5));

	}

	public List<Object[]> getTop5Customers() {
		return customerRepository.findTop5Customers(PageRequest.of(0, 5));

	}


}
