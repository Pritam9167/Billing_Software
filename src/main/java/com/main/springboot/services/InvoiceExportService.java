
package com.main.springboot.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.main.springboot.model.Invoice;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.lowagie.text.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;




@Service
public class InvoiceExportService {

    // ✅ Export to PDF
    public void exportToPdf(List<Invoice> invoices, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoices.pdf");

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Invoice Report", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Table Header
            Stream.of("ID", "Customer", "Date", "Total Amount", "GST Amount")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        table.addCell(cell);
                        
                    });

            // Table Rows
            for (Invoice invoice : invoices) {
                table.addCell(invoice.getId().toString());
                table.addCell(invoice.getCustomerName());
                table.addCell(invoice.getDate().toString());
                table.addCell(String.format("%.2f", invoice.getTotalAmount()));
                table.addCell(String.format("%.2f", invoice.getGstAmount()));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    // ✅ Export to Excel
    public void exportToExcel(List<Invoice> invoices, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Invoices");

        String[] columns = {"ID", "Customer", "Date", "Total Amount", "GST Amount"};

        // Header
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Data
        int rowNum = 1;
        for (Invoice invoice : invoices) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(invoice.getId());
            row.createCell(1).setCellValue(invoice.getCustomerName());
            row.createCell(2).setCellValue(invoice.getDate().toString());
            row.createCell(3).setCellValue(invoice.getTotalAmount());
            row.createCell(4).setCellValue(invoice.getGstAmount());
        }

        // Autosize columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=invoices.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

