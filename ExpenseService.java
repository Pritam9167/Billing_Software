package com.main.springboot.services;

import com.main.springboot.model.Expense;
import com.main.springboot.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepo;

    public ExpenseService(ExpenseRepository expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    public void saveExpense(Expense expense) {
        expenseRepo.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    public void deleteExpense(Long id) {
        expenseRepo.deleteById(id);
    }
}
