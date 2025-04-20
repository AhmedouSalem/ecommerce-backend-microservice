package com.aryan.productservice.controller.admin;

import com.aryan.productservice.dto.FAQDto;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.services.admin.AdminProductService;
import com.aryan.productservice.services.admin.faq.FAQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminProductControllerTest {

    @Mock
    private AdminProductService adminProductService;

    @Mock
    private FAQService faqService;

    @InjectMocks
    private AdminProductController controller;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .categoryId(10L)
                .build();
    }

    @Test
    void testAddProduct() throws Exception {
        when(adminProductService.addProduct(any(ProductDto.class))).thenReturn(productDto);

        ResponseEntity<ProductDto> response = controller.addProduct(productDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(productDto, response.getBody());
        verify(adminProductService).addProduct(any(ProductDto.class));
    }

    @Test
    void testGetAllProducts() {
        List<ProductDto> productList = Arrays.asList(productDto);
        when(adminProductService.getAllProducts()).thenReturn(productList);

        ResponseEntity<List<ProductDto>> response = controller.getAllProduct();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(adminProductService).getAllProducts();
    }

    @Test
    void testGetAllProductsByName() {
        when(adminProductService.getAllProductsByName("Test")).thenReturn(List.of(productDto));

        ResponseEntity<List<ProductDto>> response = controller.getAllProductByName("Test");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(productDto, response.getBody().get(0));
    }

    @Test
    void testDeleteProduct_Success() {
        when(adminProductService.deleteProduct(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(adminProductService.deleteProduct(1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testPostFAQ() {
        FAQDto faqDto = FAQDto.builder().question("Q?").answer("A").productId(1L).build();
        when(faqService.postFAQ(1L, faqDto)).thenReturn(faqDto);

        ResponseEntity<FAQDto> response = controller.postFAQ(1L, faqDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(faqDto, response.getBody());
    }

    @Test
    void testGetProductById_Found() {
        when(adminProductService.getProductById(1L)).thenReturn(productDto);

        ResponseEntity<ProductDto> response = controller.getProductById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void testGetProductById_NotFound() {
        when(adminProductService.getProductById(1L)).thenReturn(null);

        ResponseEntity<ProductDto> response = controller.getProductById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateProduct_Found() throws Exception {
        when(adminProductService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(productDto);

        ResponseEntity<ProductDto> response = controller.updateProduct(1L, productDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        when(adminProductService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(null);

        ResponseEntity<ProductDto> response = controller.updateProduct(1L, productDto);

        assertEquals(404, response.getStatusCodeValue());
    }
}
