package com.main.springboot.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.main.springboot.model.Invoice;
import com.main.springboot.model.InvoiceItem;
import com.main.springboot.model.Purchase;
import com.main.springboot.model.PurchaseItem;
import com.main.springboot.model.User;
import com.main.springboot.repository.InvoiceItemRepository;
import com.main.springboot.repository.InvoiceRepository;
import com.main.springboot.repository.PurchaseRepository;
import com.main.springboot.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;
import java.awt.Color;
import java.util.List;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

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
    public List<Map<String, Object>> getSalesReport(String type, String value) {
        List<Map<String, Object>> data = new ArrayList<>();
        User user = getLoggedInUser();

        List<Invoice> invoices;
        if (type.equalsIgnoreCase("Daily")) {
            invoices = invoiceRepository.findByDateAndCreatedBy(LocalDate.parse(value), user);
        } else if (type.equalsIgnoreCase("Monthly")) {
            YearMonth month = YearMonth.from(LocalDate.parse(value));
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();
            invoices = invoiceRepository.findByDateBetweenAndCreatedBy(start, end, user);
        } else {
            return data;
        }

        for (Invoice invoice : invoices) {
            int totalQty = invoice.getItems().stream().mapToInt(InvoiceItem::getQuantity).sum();

            Map<String, Object> row = new HashMap<>();
            row.put("name", "Invoice #" + invoice.getId());
            row.put("qty", totalQty);
            row.put("amount", invoice.getTotalAmount());
            row.put("date", invoice.getDate().toString());
            data.add(row);
        }

        return data;
    }

    @Override
    public List<Map<String, Object>> getPurchaseReport(String type, String value) {
        List<Map<String, Object>> data = new ArrayList<>();
        User user = getLoggedInUser();

        List<Purchase> purchases;
        if (type.equalsIgnoreCase("Daily")) {
            purchases = purchaseRepository.findByPurchaseDateAndCreatedBy(LocalDate.parse(value), user);
        } else if (type.equalsIgnoreCase("Monthly")) {
            YearMonth month = YearMonth.from(LocalDate.parse(value));
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();
            purchases = purchaseRepository.findByPurchaseDateBetweenAndCreatedBy(start, end, user);
        } else {
            return data;
        }

        for (Purchase purchase : purchases) {
            int totalQty = purchase.getItems().stream().mapToInt(PurchaseItem::getQuantity).sum();

            Map<String, Object> row = new HashMap<>();
            row.put("name", purchase.getSupplierName());
            row.put("qty", totalQty);
            row.put("amount", purchase.getTotalAmount());
            row.put("date", purchase.getPurchaseDate().toString());
            data.add(row);
        }

        return data;
    }

    @Override
    public List<Map<String, Object>> getProductWiseSales(String productName) {
        User user = getLoggedInUser();
        List<InvoiceItem> items = invoiceItemRepository.findByProduct_NameAndCreatedBy(productName, user);

        List<Map<String, Object>> data = new ArrayList<>();
        for (InvoiceItem item : items) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", item.getProduct().getName());
            row.put("qty", item.getQuantity());
            row.put("amount", item.getSubtotal());
            row.put("date", item.getInvoice() != null ? item.getInvoice().getDate().toString() : "N/A");
            data.add(row);
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> getCustomerHistory(String customerName) {
        User user = getLoggedInUser();
        List<Invoice> invoices = invoiceRepository.findByCustomerNameAndCreatedBy(customerName, user);

        List<Map<String, Object>> data = new ArrayList<>();
        for (Invoice invoice : invoices) {
            int totalQty = invoice.getItems().stream().mapToInt(InvoiceItem::getQuantity).sum();

            Map<String, Object> row = new HashMap<>();
            row.put("name", customerName);
            row.put("qty", totalQty);
            row.put("amount", invoice.getTotalAmount());
            row.put("date", invoice.getDate().toString());
            data.add(row);
        }

        return data;
    }

    @Override
    public void downloadPdfReport(String type, String value, HttpServletResponse response) {
        try {
            List<Map<String, Object>> reportData = getSalesReport(type, value);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("Report: " + type + " - " + value, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{3f, 1f, 2f, 2f});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Stream.of("Name", "Qty", "Amount", "Date").forEach(header ->
                    table.addCell(new PdfPCell(new Phrase(header, headFont))));

            for (Map<String, Object> row : reportData) {
                table.addCell(row.get("name").toString());
                table.addCell(row.get("qty").toString());
                table.addCell(row.get("amount").toString());
                table.addCell(row.get("date").toString());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadExcelReport(String type, String value, HttpServletResponse response) {
        try {
            List<Map<String, Object>> reportData = getSalesReport(type, value);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Qty");
            headerRow.createCell(2).setCellValue("Amount");
            headerRow.createCell(3).setCellValue("Date");

            int rowNum = 1;
            for (Map<String, Object> row : reportData) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(row.get("name").toString());
                dataRow.createCell(1).setCellValue(row.get("qty").toString());
                dataRow.createCell(2).setCellValue(row.get("amount").toString());
                dataRow.createCell(3).setCellValue(row.get("date").toString());
            }

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
