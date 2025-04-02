package com.aryan.categoryservice.services.admin;

import com.aryan.categoryservice.dto.CategoryDto;
import com.aryan.categoryservice.model.Category;
import com.aryan.categoryservice.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;


    public Category createCategory(CategoryDto categoryDto) {
        log.info("Creating a new category: {}", categoryDto.getName());
        return categoryRepository.save(
                Category.builder()
                        .name(categoryDto.getName())
                        .description(categoryDto.getDescription())
                        .build()
        );
    }


    public List<Category> getAllCategory() {
        log.info("Fetching all categories.");
        return categoryRepository.findAll();
    }


    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }


}
