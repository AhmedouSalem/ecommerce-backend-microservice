package com.aryan.categoryservice.services.admin;

import com.aryan.categoryservice.dto.CategoryDto;
import com.aryan.categoryservice.model.Category;
import com.aryan.categoryservice.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    AutoCloseable autoCloseable;
    @Mock
    CategoryRepository categoryRepository;

    private CategoryService categoryService;

    Category category;
    CategoryDto categoryDto;
    List<Category> categories;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository);
        category = Category.builder()
                .id(1L)
                .name("demoCategory")
                .build();
        categoryDto = category.getDto();
        categories = new ArrayList<>();
        categories.add(category);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createCategory() {
        when(categoryRepository.save(any())).thenReturn(category);
        assertEquals(category.getId(),categoryService.createCategory(categoryDto).getId());
    }

    @Test
    void getAllCategory() {
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category.getName(), result.get(0).getName());
    }

    @Test
    void getCategoryById() {

        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(category.getId(), result.get().getId());
        assertEquals(category.getName(), result.get().getName());
    }
}