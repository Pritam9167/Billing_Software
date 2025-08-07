package com.main.springboot.services;

import com.main.springboot.model.Expense;
import com.main.springboot.model.User;
import com.main.springboot.repository.ExpenseRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepo, UserRepository userRepository) {
        this.expenseRepo = expenseRepo;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    public void saveExpense(Expense expense) {
        expense.setCreatedBy(getLoggedInUser());
        expenseRepo.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepo.findByCreatedBy(getLoggedInUser());
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepo.findById(id)
                .filter(e -> e.getCreatedBy().equals(getLoggedInUser()))
                .orElseThrow(() -> new RuntimeException("❌ Expense not found or not allowed"));
        expenseRepo.delete(expense);
    }

    public long getExpenseCount() {
        return expenseRepo.countByCreatedBy(getLoggedInUser());
    }

    // ✅ New Methods for Chart
    public Double getTotalExpenses() {
        return Optional.ofNullable(expenseRepo.getTotalExpenses(getLoggedInUser())).orElse(0.0);
    }

    public List<Map<String, Object>> getExpensesByCategory() {
        List<Object[]> results = expenseRepo.getExpensesByCategory(getLoggedInUser());
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0] != null ? row[0] : "Uncategorized");
            map.put("amount", row[1] != null ? ((Number) row[1]).doubleValue() : 0.0);
            data.add(map);
        }
        return data;
    }
}
