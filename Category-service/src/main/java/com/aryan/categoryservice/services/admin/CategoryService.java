package com.aryan.categoryservice.services.admin;

import com.aryan.categoryservice.dto.CategoryDto;
import com.aryan.categoryservice.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    List<Category> getAllCategory();

    Optional<Category> getCategoryById(Long id);
}
