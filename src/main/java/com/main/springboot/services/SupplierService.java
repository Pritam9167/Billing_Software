package com.main.springboot.services;

import java.util.List;
import com.main.springboot.model.Supplier;

public interface SupplierService {
    void saveSupplier(Supplier supplier);
    List<Supplier> getAllSuppliers();
    Supplier getSupplierById(Long id);
    void deleteSupplier(Long id);
}
