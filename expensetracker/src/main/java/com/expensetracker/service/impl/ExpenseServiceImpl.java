package com.expensetracker.service.impl;

import com.expensetracker.dto.BudgetSummaryResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BusinessException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.mapper.EntityMapper;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ExpenseResponse createExpense(Long userId, ExpenseRequest request) {
        User user = findUserOrThrow(userId);
        Category category = validateCategoryOwnership(userId, request.getCategoryId());
        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();
        return EntityMapper.toExpenseResponse(expenseRepository.save(expense));
    }

    @Override
    public Page<ExpenseResponse> getExpenses(Long userId, int page, int size, String sortBy, String direction) {
        findUserOrThrow(userId);
        String sortField = "amount".equals(sortBy) ? "amount" : "expenseDate";
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return expenseRepository.findByUserId(userId, pageable).map(EntityMapper::toExpenseResponse);
    }

    @Override
    public ExpenseResponse getExpenseById(Long userId, Long expenseId) {
        findUserOrThrow(userId);
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        return EntityMapper.toExpenseResponse(expense);
    }

    @Override
    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        findUserOrThrow(userId);
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        Category category = validateCategoryOwnership(userId, request.getCategoryId());
        expense.setCategory(category);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        return EntityMapper.toExpenseResponse(expenseRepository.save(expense));
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        findUserOrThrow(userId);
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        expenseRepository.delete(expense);
    }

    @Override
    public Page<ExpenseResponse> searchExpenses(Long userId, Long categoryId, LocalDate startDate,
                                                 LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount,
                                                 int page, int size) {
        findUserOrThrow(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        return expenseRepository.searchExpenses(userId, categoryId, startDate, endDate, minAmount, maxAmount, pageable)
                .map(EntityMapper::toExpenseResponse);
    }

    @Override
    public List<BudgetSummaryResponse> getMonthlySummary(Long userId) {
        findUserOrThrow(userId);
        List<Category> categories = categoryRepository.findByUserId(userId);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        return categories.stream().map(category -> {
            BigDecimal totalSpent = expenseRepository.sumByCategoryAndDateRange(userId, category.getId(), startOfMonth, today);
            BigDecimal budget = category.getMonthlyBudget();
            String status;
            BigDecimal remaining = null;

            if (budget == null) {
                status = "NO_BUDGET";
            } else {
                remaining = budget.subtract(totalSpent);
                BigDecimal threshold = budget.multiply(BigDecimal.valueOf(0.8));
                if (totalSpent.compareTo(budget) > 0) {
                    status = "OVER_BUDGET";
                } else if (totalSpent.compareTo(threshold) >= 0) {
                    status = "WARNING";
                } else {
                    status = "UNDER_BUDGET";
                }
            }

            return BudgetSummaryResponse.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .monthlyBudget(budget)
                    .totalSpent(totalSpent)
                    .remainingBudget(remaining)
                    .status(status)
                    .build();
        }).toList();
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Category validateCategoryOwnership(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        if (!category.getUser().getId().equals(userId)) {
            throw new BusinessException("Category does not belong to this user");
        }
        return category;
    }
}
