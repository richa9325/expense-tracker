package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BusinessException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void createExpense_Success_WhenCategoryBelongsToUser() {
        Long userId = 1L;
        Long categoryId = 10L;

        User user = User.builder().id(userId).name("Test").email("test@test.com").build();
        Category category = Category.builder().id(categoryId).user(user).name("Food").build();
        ExpenseRequest request = new ExpenseRequest(BigDecimal.valueOf(100), "Lunch", LocalDate.now(), categoryId);

        Expense savedExpense = Expense.builder()
                .id(1L).user(user).category(category)
                .amount(request.getAmount()).description(request.getDescription())
                .expenseDate(request.getExpenseDate()).createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        ExpenseResponse response = expenseService.createExpense(userId, request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100), response.getAmount());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void createExpense_ThrowsBusinessException_WhenCategoryBelongsToDifferentUser() {
        Long userId = 1L;
        Long categoryId = 10L;
        Long otherUserId = 2L;

        User user = User.builder().id(userId).name("Test").email("test@test.com").build();
        User otherUser = User.builder().id(otherUserId).name("Other").email("other@test.com").build();
        Category category = Category.builder().id(categoryId).user(otherUser).name("Food").build();
        ExpenseRequest request = new ExpenseRequest(BigDecimal.valueOf(100), "Lunch", LocalDate.now(), categoryId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(BusinessException.class, () -> expenseService.createExpense(userId, request));
        verify(expenseRepository, never()).save(any(Expense.class));
    }
}
