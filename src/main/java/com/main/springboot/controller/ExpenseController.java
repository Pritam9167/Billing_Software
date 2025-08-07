package com.main.springboot.controller;

import com.main.springboot.model.Expense;
import com.main.springboot.services.ExpenseService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // Show add form
    @GetMapping("/add")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "add_expense";
    }

    // Save expense
    @PostMapping("/save")
    public String saveExpense(@ModelAttribute Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        expenseService.saveExpense(expense);
        return "redirect:/expenses/list";
    }

    // View list
    @GetMapping("/list")
    public String listExpenses(Model model) {
        List<Expense> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);

        // Prepare data for Chart.js (only name + amount)
        List<Map<String, Object>> chartData = expenses.stream().map(exp -> {
            Map<String, Object> map = new HashMap<>();
            map.put("expenseName", exp.getExpenseName());
            map.put("amount", exp.getAmount());
            return map;
        }).toList();

        model.addAttribute("chartData", chartData);
        return "expenses";
    }


    // Delete
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "redirect:/expenses/list";
    }

    // ✅ Export to Excel
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.xlsx");

        List<Expense> expenses = expenseService.getAllExpenses();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Expenses");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Category");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Date");
        header.createCell(4).setCellValue("Notes");

        int rowCount = 1;
        for (Expense exp : expenses) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(exp.getExpenseName());
            row.createCell(1).setCellValue(exp.getCategory());
            row.createCell(2).setCellValue(exp.getAmount());
            row.createCell(3).setCellValue(exp.getDate().toString());
            row.createCell(4).setCellValue(exp.getNotes());
        }

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");

        List<Expense> expenses = expenseService.getAllExpenses();

        // Step 1: Create Document and PdfWriter
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Step 2: Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Expense Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Step 3: Table
            PdfPTable table = new PdfPTable(5); // 5 columns
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{2f, 2f, 1.5f, 2f, 3f}); // Must match number of columns

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            String[] headers = {"Name", "Category", "Amount", "Date", "Notes"};
            for (String h : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(h, headFont));
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            for (Expense exp : expenses) {
                table.addCell(new Phrase(exp.getExpenseName()));
                table.addCell(new Phrase(exp.getCategory() != null ? exp.getCategory() : ""));
                table.addCell(new Phrase(String.valueOf(exp.getAmount())));
                table.addCell(new Phrase(exp.getDate() != null ? exp.getDate().toString() : ""));
                table.addCell(new Phrase(exp.getNotes() != null ? exp.getNotes() : ""));
            }

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace(); // Log or throw error properly
        } finally {
            document.close();
        }
    }

}