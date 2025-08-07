package com.main.springboot.services;

import com.main.springboot.model.Customer;
import com.main.springboot.model.User;
import com.main.springboot.repository.CustomerRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    public void saveCustomer(Customer customer) {
        customer.setCreatedBy(getLoggedInUser());  // attach user
        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findByCreatedBy(getLoggedInUser());
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .filter(c -> c.getCreatedBy().equals(getLoggedInUser())) // security check
                .orElse(null);
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer != null) {
            customerRepository.delete(customer);
        }
    }

    public List<Object[]> getTop5Customers() {
        return customerRepository.findTop5Customers(getLoggedInUser(),
                org.springframework.data.domain.PageRequest.of(0, 5));
    }
}
