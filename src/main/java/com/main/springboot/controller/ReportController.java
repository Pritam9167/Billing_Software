
package com.main.springboot.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.main.springboot.services.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Report selection page
    @GetMapping
    public String showReportOptions() {
        return "reports";
    }

    // Generate sales report (Daily or Monthly)
    @GetMapping("/sales")
    public String generateSalesReport(@RequestParam String type, @RequestParam String value, Model model) {
        model.addAttribute("reportData", reportService.getSalesReport(type, value));
        model.addAttribute("title", type + " Sales Report: " + value);
        return "report_result";
    }

    // Generate purchase report (Daily or Monthly)
    @GetMapping("/purchases")
    public String generatePurchaseReport(@RequestParam String type, @RequestParam String value, Model model) {
        model.addAttribute("reportData", reportService.getPurchaseReport(type, value));
        model.addAttribute("title", type + " Purchase Report: " + value);
        return "report_result";
    }

    // Product-wise Sales
    @GetMapping("/product-sales")
    public String productWiseSales(@RequestParam String productName, Model model) {
        model.addAttribute("reportData", reportService.getProductWiseSales(productName));
        model.addAttribute("title", "Sales for Product: " + productName);
        return "report_result";
    }

    // Customer Purchase History
    @GetMapping("/customer-history")
    public String customerHistory(@RequestParam String customerName, Model model) {
        model.addAttribute("reportData", reportService.getCustomerHistory(customerName));
        model.addAttribute("title", "Customer Purchase History: " + customerName);
        return "report_result";
    }

    // PDF Download
    @GetMapping("/download/pdf")
    public void downloadPdf(@RequestParam String type, @RequestParam String value, HttpServletResponse response) {
        reportService.downloadPdfReport(type, value, response);
    }

    // Excel Download
    @GetMapping("/download/excel")
    public void downloadExcel(@RequestParam String type, @RequestParam String value, HttpServletResponse response) {
        reportService.downloadExcelReport(type, value, response);
    }
}
