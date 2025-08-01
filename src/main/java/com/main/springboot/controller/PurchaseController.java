
package com.main.springboot.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.main.springboot.model.Purchase;
import com.main.springboot.model.PurchaseItem;
import com.main.springboot.services.ProductService;
import com.main.springboot.services.PurchaseService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String showPurchaseForm(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("productList", productService.getAllProducts());
        return "purchase_form";
    }

    @PostMapping("/save")
    public String savePurchase(@ModelAttribute Purchase purchase) {
        purchaseService.savePurchase(purchase);
        return "redirect:/purchases";
    }

    @GetMapping
    public String listPurchases(Model model) {
        model.addAttribute("purchaseList", purchaseService.getAllPurchases());
        return "purchases";
    }

    @GetMapping("/bill/{id}")
    public void generateBill(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Purchase purchase = purchaseService.getPurchaseById(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=purchase_bill_" + id + ".pdf");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("PURCHASE INVOICE", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // spacing
            document.add(new Paragraph("Supplier: " + purchase.getSupplierName(), normalFont));
            document.add(new Paragraph("Purchase Date: " + purchase.getPurchaseDate(), normalFont));
            document.add(new Paragraph("Payment Mode: " + purchase.getPaymentMode(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Product");
            table.addCell("Quantity");
            table.addCell("Price");
            table.addCell("Subtotal");

            double grandTotal = 0;

            for (PurchaseItem item : purchase.getItems()) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("₹" + item.getPrice());
                table.addCell("₹" + item.getSubtotal());
                grandTotal += item.getSubtotal();
            }

            document.add(table);
            document.add(new Paragraph(" "));

            Paragraph totalPara = new Paragraph("Grand Total: ₹" + grandTotal, titleFont);
            totalPara.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(totalPara);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your business!", normalFont));
            document.add(new Paragraph("XYZ Enterprises, Mumbai", normalFont));
            document.add(new Paragraph("support@xyz.com | +91-9876543210", normalFont));

            document.close();
        } catch (Exception e) {
            e.printStackTrace(); // Print full error in logs
            throw new IOException("❌ Error generating PDF: " + e.getMessage());
        }
    }


}
