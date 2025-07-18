package com.main.springboot.controller;

import com.main.springboot.model.Product;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.services.ProductService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {
	 @Autowired
	    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewProducts(Model model) {
        model.addAttribute("productList", productService.getAllProducts());
        return "products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "product_form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
    
    
    @GetMapping("/stock-summary")
    public List<Map<String, Object>> getStockSummary() {
        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> summary = new ArrayList<>();

        for (Product p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", p.getName());
            map.put("stock", p.getQuantityInStock());
            summary.add(map);
        }

        return summary;
    }

}
