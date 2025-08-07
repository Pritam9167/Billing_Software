
package com.main.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.main.springboot.model.Supplier;
import com.main.springboot.services.ExportService;
import com.main.springboot.services.SupplierService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

	
	 @Autowired
	    private ExportService exportService;
	
    @Autowired
    private SupplierService supplierService;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier_form";
    }

    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute Supplier supplier) {
        supplierService.saveSupplier(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping
    public String viewSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "supplier_list";
    }
    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id);
        model.addAttribute("supplier", supplier);
        return "supplier_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return "redirect:/suppliers";
    }
   

    @GetMapping("/export/pdf")
    public void exportPDF(HttpServletResponse response) throws Exception {
        List<Supplier> list = supplierService.getAllSuppliers();
        exportService.exportSuppliersToPDF(list, response);
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<Supplier> list = supplierService.getAllSuppliers();
        exportService.exportSuppliersToExcel(list, response);
    }

}
