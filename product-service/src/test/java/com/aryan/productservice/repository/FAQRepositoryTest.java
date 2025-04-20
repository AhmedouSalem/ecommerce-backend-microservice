package com.aryan.productservice.repository;

import com.aryan.productservice.config.FeignClientInterceptor;
import com.aryan.productservice.dto.CategoryResponse;
import com.aryan.productservice.model.FAQ;
import com.aryan.productservice.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Slf4j
@Transactional
class FAQRepositoryTest {

    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        tearDown();

        CategoryResponse fakeCategory = CategoryResponse.builder()
                .id(1L)
                .name("Tech")
                .description("Tech category")
                .build();

        MultipartFile image = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "fake image".getBytes());

        product = Product.builder()
                .name("Laptop")
                .price(1500L)
                .img(image.getBytes())
                .description("Powerful laptop")
                .categoryId(fakeCategory.getId())
                .build();

        product = productRepository.save(product);

        // 📘 Ajouter deux FAQs
        FAQ faq1 = FAQ.builder()
                .question("Is it fast?")
                .answer("Yes, very fast.")
                .product(product)
                .build();

        FAQ faq2 = FAQ.builder()
                .question("Battery life?")
                .answer("Up to 12 hours.")
                .product(product)
                .build();

        faqRepository.save(faq1);
        faqRepository.save(faq2);
    }

    @Test
    void findAllByProductId_shouldReturnFAQs() {
        List<FAQ> faqs = faqRepository.findAllByProductId(product.getId());

        assertNotNull(faqs);
        assertEquals(2, faqs.size());

        for (FAQ faq : faqs) {
            assertEquals(product.getId(), faq.getProduct().getId());
        }
    }

    @AfterEach
    void tearDown() {
        faqRepository.deleteAll();
        productRepository.deleteAll();
    }
}
