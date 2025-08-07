
package com.main.springboot.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.main.springboot.model.Invoice;
import com.main.springboot.model.Supplier;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.lowagie.text.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@Service
public class ExportService {

    public void exportSuppliersToPDF(List<Supplier> suppliers, HttpServletResponse response) throws Exception {
        Document document = new Document();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=suppliers.pdf");

        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Supplier List", font);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell("Name");
        table.addCell("Phone");
        table.addCell("Email");
        table.addCell("Address");
        table.addCell("GSTIN");

        for (Supplier s : suppliers) {
            table.addCell(s.getName());
            table.addCell(s.getPhone());
            table.addCell(s.getEmail());
            table.addCell(s.getAddress());
            table.addCell(s.getGstin());
        }

        document.add(table);
        document.close();
    }

    public void exportSuppliersToExcel(List<Supplier> suppliers, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=suppliers.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Suppliers");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Phone");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Address");
        header.createCell(4).setCellValue("GSTIN");

        int rowNum = 1;
        for (Supplier s : suppliers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getName());
            row.createCell(1).setCellValue(s.getPhone());
            row.createCell(2).setCellValue(s.getEmail());
            row.createCell(3).setCellValue(s.getAddress());
            row.createCell(4).setCellValue(s.getGstin());
        }

        ServletOutputStream os = response.getOutputStream();

        workbook.write(os);
        workbook.close();
        os.close();
    }
}
