package com.aryan.productservice.controller.customer;

import com.aryan.productservice.dto.FAQDto;
import com.aryan.productservice.dto.ProductDetailDto;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.services.customer.CustomerProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerProductControllerTest {

    @Mock
    private CustomerProductService customerProductService;

    @InjectMocks
    private CustomerProductController controller;

    private ProductDto productDto;
    private ProductDetailDto productDetailDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .categoryId(10L)
                .build();
        FAQDto faqDto = FAQDto.builder()
                .question("Is it waterproof?")
                .answer("Yes")
                .build();

        productDetailDto = ProductDetailDto.builder()
                .productDto(productDto)
                .faqDtoList(List.of(faqDto))
                .build();
    }

    @Test
    void testGetAllProduct() {
        when(customerProductService.getAllProducts()).thenReturn(List.of(productDto));

        ResponseEntity<List<ProductDto>> response = controller.getAllProduct();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productDto, response.getBody().get(0));

        verify(customerProductService).getAllProducts();
    }

    @Test
    void testGetAllProductByName() {
        when(customerProductService.getAllProductsByName("Test")).thenReturn(List.of(productDto));

        ResponseEntity<List<ProductDto>> response = controller.getAllProductByName("Test");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().get(0).getName());

        verify(customerProductService).getAllProductsByName("Test");
    }

    @Test
    void testGetProductDetailById_Found() {
        when(customerProductService.getProductDetailById(1L)).thenReturn(productDetailDto);

        ResponseEntity<ProductDetailDto> response = controller.getProductDetailById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getProductDto().getName());

        verify(customerProductService).getProductDetailById(1L);
    }

    @Test
    void testGetProductDetailById_NotFound() {
        when(customerProductService.getProductDetailById(1L)).thenReturn(null);

        ResponseEntity<ProductDetailDto> response = controller.getProductDetailById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(customerProductService).getProductDetailById(1L);
    }

    @Test
    void testGetProductByProductId_Found() {
        when(customerProductService.getProductById(1L)).thenReturn(productDto);

        ResponseEntity<ProductDto> response = controller.getProductByProductId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());

        verify(customerProductService).getProductById(1L);
    }

    @Test
    void testGetProductByProductId_NotFound() {
        when(customerProductService.getProductById(1L)).thenReturn(null);

        ResponseEntity<ProductDto> response = controller.getProductByProductId(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(customerProductService).getProductById(1L);
    }
}
