package com.aryan.productservice.services.admin.faq;

import com.aryan.productservice.dto.FAQDto;
import com.aryan.productservice.model.FAQ;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.FAQRepository;
import com.aryan.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FAQServiceImplementTest {

    @Mock
    private FAQRepository faqRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FAQServiceImplement faqServiceImplement;

    private Product mockProduct;
    private FAQ mockFAQ;
    private FAQDto faqDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(100L)
                .build();

        mockFAQ = FAQ.builder()
                .id(1L)
                .question("What is it?")
                .answer("A test product.")
                .product(mockProduct)
                .build();

        faqDto = FAQDto.builder()
                .question("What is it?")
                .answer("A test product.")
                .build();
    }

    @Test
    void postFAQ_ProductExists_ShouldSaveAndReturnFAQDto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(faqRepository.save(any(FAQ.class))).thenReturn(mockFAQ);

        FAQDto result = faqServiceImplement.postFAQ(1L, faqDto);

        assertNotNull(result);
        assertEquals(faqDto.getQuestion(), result.getQuestion());
        assertEquals(faqDto.getAnswer(), result.getAnswer());
        assertEquals(mockProduct.getId(), result.getProductId());

        verify(productRepository, times(1)).findById(1L);
        verify(faqRepository, times(1)).save(any(FAQ.class));
    }

    @Test
    void postFAQ_ProductDoesNotExist_ShouldReturnNull() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        FAQDto result = faqServiceImplement.postFAQ(99L, faqDto);

        assertNull(result);
        verify(productRepository, times(1)).findById(99L);
        verify(faqRepository, never()).save(any());
    }
}
