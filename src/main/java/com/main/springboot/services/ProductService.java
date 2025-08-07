
package com.main.springboot.services;

import com.main.springboot.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    void saveProduct(Product product);
    void deleteProduct(Long id);
    List<Object[]> getTop5Products();

}
