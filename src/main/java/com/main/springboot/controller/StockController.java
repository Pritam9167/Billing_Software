package com.main.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.main.springboot.model.Product;
import com.main.springboot.model.User;
import com.main.springboot.repository.ProductRepository;
import com.main.springboot.repository.UserRepository;
import com.main.springboot.services.StockConfigService;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockConfigService stockConfigService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get Logged-in User
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    @GetMapping("/low")
    public String showLowStock(Model model) {
        User user = getLoggedInUser();
        int threshold = stockConfigService.getThreshold();

        // ✅ Fetch only products created by this user
        List<Product> lowStockProducts = productRepository.findByCreatedByAndQuantityInStockLessThanEqual(user, threshold);

        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("threshold", threshold);
        return "low_stock";
    }

    @GetMapping("/low/pdf")
    public void exportLowStockToPdf(HttpServletResponse response) throws Exception {
        User user = getLoggedInUser();
        int threshold = stockConfigService.getThreshold();
        List<Product> lowStockProducts = productRepository.findByCreatedByAndQuantityInStockLessThanEqual(user, threshold);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=low_stock.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Low Stock Products", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{4f, 2f, 2f});

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        table.addCell(new PdfPCell(new Phrase("Product Name", headFont)));
        table.addCell(new PdfPCell(new Phrase("Stock", headFont)));
        table.addCell(new PdfPCell(new Phrase("Price", headFont)));

        for (Product p : lowStockProducts) {
            table.addCell(p.getName());
            table.addCell(String.valueOf(p.getQuantityInStock()));
            table.addCell(String.valueOf(p.getPrice()));
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/low/excel")
    public void exportLowStockToExcel(HttpServletResponse response) throws Exception {
        User user = getLoggedInUser();
        int threshold = stockConfigService.getThreshold();
        List<Product> lowStockProducts = productRepository.findByCreatedByAndQuantityInStockLessThanEqual(user, threshold);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=low_stock.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Low Stock");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Product Name");
        header.createCell(1).setCellValue("Stock");
        header.createCell(2).setCellValue("Price");

        int rowNum = 1;
        for (Product p : lowStockProducts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getName());
            row.createCell(1).setCellValue(p.getQuantityInStock());
            row.createCell(2).setCellValue(p.getPrice());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
