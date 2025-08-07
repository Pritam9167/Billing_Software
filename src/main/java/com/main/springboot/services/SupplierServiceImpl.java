package com.main.springboot.services;

import com.main.springboot.model.Supplier;
import com.main.springboot.model.User;
import com.main.springboot.repository.SupplierRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get logged-in user
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    @Override
    public void saveSupplier(Supplier supplier) {
        supplier.setCreatedBy(getLoggedInUser()); // attach current user
        supplierRepository.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByCreatedBy(getLoggedInUser());
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .filter(s -> s.getCreatedBy().equals(getLoggedInUser())) // ensure user owns it
                .orElse(null);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        if (supplier != null) {
            supplierRepository.delete(supplier);
        }
    }
}
 