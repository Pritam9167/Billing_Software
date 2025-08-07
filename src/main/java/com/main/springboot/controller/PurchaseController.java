
package com.main.springboot.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.main.springboot.model.Purchase;
import com.main.springboot.model.PurchaseItem;
import com.main.springboot.services.ProductService;
import com.main.springboot.services.PurchaseService;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
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
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // ====== Fonts ======
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(44, 62, 80));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, new Color(100, 100, 100));

            // ====== Add Logo ======
            try {
                Image logo = Image.getInstance("src/main/resources/static/images/logo.png"); // <-- path to logo
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                System.out.println("⚠ Logo not found, skipping logo.");
            }

            // ====== Company Header ======
            Paragraph companyName = new Paragraph("XYZ Enterprises", titleFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);

            Paragraph companyDetails = new Paragraph("Nagpur, Maharashtra | support@xyz.com | +91-9876543210", normalFont);
            companyDetails.setAlignment(Element.ALIGN_CENTER);
            document.add(companyDetails);

            document.add(Chunk.NEWLINE);

            Paragraph invoiceTitle = new Paragraph("PURCHASE INVOICE", titleFont);
            invoiceTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(invoiceTitle);

            document.add(Chunk.NEWLINE);

            // ====== Supplier Info ======
            PdfPTable supplierTable = new PdfPTable(2);
            supplierTable.setWidthPercentage(100);
            supplierTable.setSpacingBefore(10);

            supplierTable.addCell(getCell("Supplier:", PdfPCell.ALIGN_LEFT, normalFont));
            supplierTable.addCell(getCell(purchase.getSupplierName(), PdfPCell.ALIGN_LEFT, normalFont));

            supplierTable.addCell(getCell("Purchase Date:", PdfPCell.ALIGN_LEFT, normalFont));
            supplierTable.addCell(getCell(String.valueOf(purchase.getPurchaseDate()), PdfPCell.ALIGN_LEFT, normalFont));

            supplierTable.addCell(getCell("Payment Mode:", PdfPCell.ALIGN_LEFT, normalFont));
            supplierTable.addCell(getCell(purchase.getPaymentMode(), PdfPCell.ALIGN_LEFT, normalFont));

            document.add(supplierTable);

            document.add(Chunk.NEWLINE);

            // ====== Items Table ======
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1f, 2f, 2f});

            String[] headers = {"Product", "Quantity", "Price (₹)", "Subtotal (₹)"};
            for (String h : headers) {
                PdfPCell header = new PdfPCell(new Phrase(h, headerFont));
                header.setBackgroundColor(new Color(52, 152, 219));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(8);
                table.addCell(header);
            }

            double grandTotal = 0;
            for (PurchaseItem item : purchase.getItems()) {
                table.addCell(getCell(item.getProduct().getName(), PdfPCell.ALIGN_LEFT, normalFont));
                table.addCell(getCell(String.valueOf(item.getQuantity()), PdfPCell.ALIGN_CENTER, normalFont));
                table.addCell(getCell("₹" + item.getPrice(), PdfPCell.ALIGN_RIGHT, normalFont));
                table.addCell(getCell("₹" + item.getSubtotal(), PdfPCell.ALIGN_RIGHT, normalFont));
                grandTotal += item.getSubtotal();
            }

            document.add(table);

            document.add(Chunk.NEWLINE);

            // ====== Total Section ======
            Paragraph totalPara = new Paragraph("Grand Total: ₹" + grandTotal, titleFont);
            totalPara.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(totalPara);

            document.add(Chunk.NEWLINE);

            // ====== Footer ======
            LineSeparator line = new LineSeparator();
            document.add(line);

            Paragraph footer = new Paragraph("Thank you for your business!\nPlease contact us for any queries.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("❌ Error generating PDF: " + e.getMessage());
        }
    }

    // Helper
    private PdfPCell getCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


}
