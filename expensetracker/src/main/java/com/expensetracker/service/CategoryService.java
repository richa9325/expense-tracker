package com.expensetracker.service;

import com.expensetracker.dto.CategoryRequest;
import com.expensetracker.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(Long userId, CategoryRequest request);
    List<CategoryResponse> getCategoriesByUser(Long userId);
    CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request);
    void deleteCategory(Long userId, Long categoryId);
}
