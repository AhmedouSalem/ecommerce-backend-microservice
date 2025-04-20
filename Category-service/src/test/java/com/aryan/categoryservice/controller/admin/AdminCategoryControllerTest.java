package com.aryan.categoryservice.controller.admin;


import com.aryan.categoryservice.dto.CategoryDto;
import com.aryan.categoryservice.model.Category;
import com.aryan.categoryservice.services.admin.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(AdminCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private CategoryDto categoryDto;
    List<Category> categories;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CategoryService categoryService() {
            return mock(CategoryService.class);
        }
    }

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .name("cat1")
                .description("cat1_description")
                .build();
        category2 = Category.builder()
                .name("cat2")
                .description("cat2_description")
                .build();
        categoryDto = category1.getDto();
        categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCategory() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJSON = objectWriter.writeValueAsString(categoryDto);
        log.info(requestJSON);

        when(categoryService.createCategory(categoryDto)).thenReturn(category1);
        log.info(objectWriter.writeValueAsString(category1));

        mockMvc.perform(post("/api/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andDo(print()).andExpect(status().isCreated());

    }

    @Test
    void getAllCategory() throws Exception {
        when(categoryService.getAllCategory()).thenReturn(categories);
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category1));
        mockMvc.perform(get("/api/admin/category/1"))
                .andExpect(status().isOk());
    }
}