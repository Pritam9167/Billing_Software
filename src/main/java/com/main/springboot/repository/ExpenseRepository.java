package com.main.springboot.repository;

import com.main.springboot.model.Expense;
import com.main.springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCreatedBy(User user);
    long countByCreatedBy(User user);

    // ✅ Total Expenses for user
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.createdBy = :user")
    Double getTotalExpenses(User user);

    // ✅ Group by category
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.createdBy = :user GROUP BY e.category")
    List<Object[]> getExpensesByCategory(User user);
}
