package com.main.springboot.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.main.springboot.model.User;
import com.main.springboot.services.DashboardServices;
import com.main.springboot.services.InvoiceService;
import com.main.springboot.services.ProductService;
import com.main.springboot.services.PurchaseService;
import com.main.springboot.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class DashboardController {

    @Autowired
    private DashboardServices dashboardService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    // ✅ Merged into a single dashboard method
    @GetMapping("/")
    public String showDashboard(Model model) {
        User user = userService.getLoggedInUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());

        // Dashboard stats
        model.addAttribute("totalSales", dashboardService.getTotalSales());
        model.addAttribute("totalPurchases", dashboardService.getTotalPurchases());
        model.addAttribute("totalProducts", dashboardService.getTotalProducts());
        model.addAttribute("totalCustomers", dashboardService.getTotalCustomers());
        model.addAttribute("totalInvoices", dashboardService.getTotalInvoices());
        model.addAttribute("totalGST", dashboardService.getTotalGST());

        // Top Products and Customers
        List<Object[]> topProducts = productService.getTop5Products();
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("topCustomers", dashboardService.getTop5Customers());

        // For Bar Chart: Sales vs Purchases
        model.addAttribute("totalSales", invoiceService.getTotalSales());
        model.addAttribute("totalPurchases", purchaseService.getTotalPurchases());

        // For Pie Chart: GST vs Net Sales
        model.addAttribute("totalGST", invoiceService.getTotalGSTCollected());
        model.addAttribute("netSales", invoiceService.getNetSales());

        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String redirectToMain() {
        return "redirect:/";
    }

    @GetMapping("/dashboard/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=dashboard.pdf");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new Paragraph("🔷 Dashboard Report"));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("Total Sales: ₹" + dashboardService.getTotalSales()));
            document.add(new Paragraph("Total Purchases: ₹" + dashboardService.getTotalPurchases()));
            document.add(new Paragraph("Total Products: " + dashboardService.getTotalProducts()));
            document.add(new Paragraph("Total Customers: " + dashboardService.getTotalCustomers()));
            document.add(new Paragraph("Total Invoices: " + dashboardService.getTotalInvoices()));
            document.add(new Paragraph("Total GST Collected: ₹" + dashboardService.getTotalGST()));
            document.add(new Paragraph("------------------------------------------------------------"));

            document.add(new Paragraph("🟢 Top 5 Products (By Quantity Sold):"));
            dashboardService.getTop5Products().forEach(p -> {
                String name = String.valueOf(p[0]);
                String qty = String.valueOf(p[1]);
                try {
                    document.add(new Paragraph(name + " - Qty: " + qty));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            });

            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("🟣 Top 5 Customers (By Purchase Amount):"));
            dashboardService.getTop5Customers().forEach(c -> {
                String name = String.valueOf(c[0]);
                String amount = String.valueOf(c[1]);
                try {
                    document.add(new Paragraph(name + " - ₹" + amount));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            });

            document.close();

        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }
}
