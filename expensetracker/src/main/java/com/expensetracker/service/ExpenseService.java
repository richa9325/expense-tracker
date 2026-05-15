package com.expensetracker.service;

import com.expensetracker.dto.BudgetSummaryResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(Long userId, ExpenseRequest request);
    Page<ExpenseResponse> getExpenses(Long userId, int page, int size, String sortBy, String direction);
    ExpenseResponse getExpenseById(Long userId, Long expenseId);
    ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request);
    void deleteExpense(Long userId, Long expenseId);
    Page<ExpenseResponse> searchExpenses(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount, int page, int size);
    List<BudgetSummaryResponse> getMonthlySummary(Long userId);
}
