package com.main.springboot.services;

import com.main.springboot.model.Product;
import com.main.springboot.model.User;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findByCreatedBy(getLoggedInUser());
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .filter(p -> p.getCreatedBy().equals(getLoggedInUser())) // ensure user owns it
                .orElse(null);
    }

    @Override
    public void saveProduct(Product product) {
        product.setCreatedBy(getLoggedInUser());
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        if (product != null) {
            productRepository.delete(product);
        }
    }

    @Override
    public List<Object[]> getTop5Products() {
        return productRepository.findTop5Products(getLoggedInUser(), PageRequest.of(0, 5));
    }
}
