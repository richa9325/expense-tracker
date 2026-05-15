package com.expensetracker.service.impl;

import com.expensetracker.dto.CategoryRequest;
import com.expensetracker.dto.CategoryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BusinessException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.mapper.EntityMapper;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = findUserOrThrow(userId);
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(userId, request.getName())) {
            throw new BusinessException("Category '" + request.getName() + "' already exists for this user");
        }
        Category category = Category.builder()
                .user(user)
                .name(request.getName())
                .monthlyBudget(request.getMonthlyBudget())
                .build();
        return EntityMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getCategoriesByUser(Long userId) {
        findUserOrThrow(userId);
        return categoryRepository.findByUserId(userId).stream()
                .map(EntityMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        findUserOrThrow(userId);
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        category.setName(request.getName());
        category.setMonthlyBudget(request.getMonthlyBudget());
        return EntityMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long userId, Long categoryId) {
        findUserOrThrow(userId);
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        long expenseCount = expenseRepository.countByCategoryId(categoryId);
        if (expenseCount > 0) {
            throw new BusinessException("Cannot delete category: it has " + expenseCount + " associated expenses.");
        }
        categoryRepository.delete(category);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
