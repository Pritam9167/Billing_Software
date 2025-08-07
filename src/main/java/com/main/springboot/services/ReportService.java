
package com.main.springboot.services;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, Object>> getSalesReport(String type, String value);

    List<Map<String, Object>> getPurchaseReport(String type, String value);

    List<Map<String, Object>> getProductWiseSales(String productName);

    List<Map<String, Object>> getCustomerHistory(String customerName);

    void downloadPdfReport(String type, String value, HttpServletResponse response);

    void downloadExcelReport(String type, String value, HttpServletResponse response);
}
