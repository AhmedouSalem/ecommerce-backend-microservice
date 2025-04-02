package com.aryan.categoryservice;

import com.aryan.categoryservice.dto.CategoryDto;
import com.aryan.categoryservice.services.admin.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CategoryServiceApplication {

    @Autowired
    private CategoryService cr ;

    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApplication.class, args);

    }

    public CommandLineRunner init() {
        return args -> {
            CategoryDto category = new CategoryDto();
            category.setName("Fitness");
            category.setDescription("Fitness description");

            cr.createCategory(category);
        };
    }
}
