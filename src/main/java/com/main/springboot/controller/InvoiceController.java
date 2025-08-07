package com.main.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.main.springboot.model.Invoice;
import com.main.springboot.model.InvoiceItem;
import com.main.springboot.model.Product;
import com.main.springboot.services.InvoiceExportService;
import com.main.springboot.services.InvoiceService;
import com.main.springboot.services.ProductService;

import jakarta.servlet.http.HttpServletResponse;



@Controller
@RequestMapping("/invoices")
public class InvoiceController {

	@Autowired
	private InvoiceExportService invoiceExportService;

	
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String showInvoiceForm(Model model) {
        model.addAttribute("invoice", new Invoice());

        List<Product> products = productService.getAllProducts();
        model.addAttribute("productList", products);

        return "invoice_form";
    }

    @PostMapping("/save")
    public String saveInvoice(@ModelAttribute Invoice invoice) {
        // Link each item back to invoice (important for @OneToMany mapping)
        if (invoice.getItems() != null) {
            for (InvoiceItem item : invoice.getItems()) {
                item.setInvoice(invoice);
            }
        }
        
        invoiceService.saveInvoice(invoice);
        return "redirect:/invoices";
    }


    @GetMapping
    public String listInvoices(Model model) {
        List<Invoice> invoices = invoiceService.getAllInvoices();

        // Force loading items if fetch = LAZY
        for (Invoice invoice : invoices) {
            invoice.getItems().size(); // initializes the collection
        }

        model.addAttribute("invoiceList", invoices);
        return "invoices";
    }

    
    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            invoiceExportService.exportToPdf(invoices, response);
        } catch (Exception e) {
            e.printStackTrace(); // for debugging in console
        }
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            invoiceExportService.exportToExcel(invoices, response);
        } catch (Exception e) {
            e.printStackTrace(); // for debugging in console
        }
    }



    
}