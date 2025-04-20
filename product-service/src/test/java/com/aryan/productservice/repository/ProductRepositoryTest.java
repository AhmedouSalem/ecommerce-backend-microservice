package com.aryan.productservice.repository;

import com.aryan.productservice.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws IOException {
        tearDown();

        Long categoryId = 1L;
        MultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());

        for (int i = 0; i < 10; i++) {
            Product product = Product.builder()
                    .name(i < 5 ? "low" : "high")
                    .price(200L)
                    .img(mockMultipartFile.getBytes())
                    .categoryId(categoryId)
                    .description("demoDescription")
                    .build();
            productRepository.save(product);
        }
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findAllByNameContaining_Found() {
        List<Product> products = productRepository.findAllByNameContaining("low");
        assertEquals(5, products.size());
    }

    @Test
    void findAllByNameContaining_NotFound(){
        List<Product> products = productRepository.findAllByNameContaining("incorrect");
        assertTrue(products.isEmpty());
    }
}
