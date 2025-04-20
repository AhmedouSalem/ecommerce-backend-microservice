package com.aryan.productservice.services.admin.product;

import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.dto.CategoryResponse;
import com.aryan.productservice.feign.CategoryClient;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.ProductRepository;
import com.aryan.productservice.services.admin.AdminProductService;
import com.aryan.productservice.services.admin.AdminProductServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminProductServiceImplTest {

    AutoCloseable autoCloseable;
    Product product;
    Product savedProduct;
    Product updatedProduct;
    ProductDto productDto;
    CategoryResponse categoryResponse;
    List<Product> products;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryClient categoryClient;

    private AdminProductService adminProductService;

    MultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() throws IOException {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminProductService = new AdminProductServiceImpl(productRepository, categoryClient);

        mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test image".getBytes());

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("demoCategory")
                .description("demoDescription")
                .build();

        product = Product.builder()
                .id(1L)
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(categoryResponse)
                .description("demoDescription")
                .build();

        savedProduct = Product.builder()
                .id(2L)
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(categoryResponse)
                .description("demoDescription")
                .build();

        updatedProduct = Product.builder()
                .id(3L)
                .name("demoName")
                .price(200L)
                .img(mockMultipartFile.getBytes())
                .category(categoryResponse)
                .description("updatedDescription")
                .build();

        productDto = ProductDto.builder()
                .name("demoName")
                .description("demoDescription")
                .categoryId(categoryResponse.getId())
                .categoryName(categoryResponse.getName())
                .price(200L)
                .img(mockMultipartFile)
                .build();

        products = new ArrayList<>();
        products.add(product);
        products.add(savedProduct);
        products.add(updatedProduct);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addProduct() throws Exception {
        when(categoryClient.findById(1L)).thenReturn(categoryResponse);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product inputProduct = invocation.getArgument(0);
            inputProduct.setId(999L);
            return inputProduct;
        });

        ProductDto result = adminProductService.addProduct(productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        assertEquals(productDto.getCategoryId(), result.getCategoryId());
        assertEquals(productDto.getCategoryName(), result.getCategoryName());
    }


    @Test
    void getAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(products);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Demo Category");

        when(categoryClient.findById(any())).thenReturn(categoryResponse);

        // Act
        List<ProductDto> allProducts = adminProductService.getAllProducts();

        // Assert
        assertEquals(3, allProducts.size());
        assertEquals(updatedProduct.getDescription(), allProducts.get(2).getDescription());
    }


    @Test
    void getAllProducts_NoProductFound() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(adminProductService.getAllProducts().isEmpty());
    }

    @Test
    void getAllProductsByName() {
        // Arrange
        Product product1 = Product.builder().id(1L).name("demoName1").categoryId(1L).build();
        Product product2 = Product.builder().id(2L).name("demoName2").categoryId(1L).build();
        Product product3 = Product.builder().id(3L).name("demoName3").categoryId(1L).build();
        List<Product> products = List.of(product1, product2, product3);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Demo Category");

        when(productRepository.findAllByNameContaining("demoName")).thenReturn(products);
        when(categoryClient.findById(any())).thenReturn(categoryResponse);

        // Act
        List<ProductDto> foundProducts = adminProductService.getAllProductsByName("demoName");

        // Assert
        assertEquals(3, foundProducts.size());
        for (ProductDto product : foundProducts) {
            assertTrue(product.getName().contains("demoName"));
        }

        when(productRepository.findAllByNameContaining("nonExistentKeyword")).thenReturn(List.of());
        assertTrue(adminProductService.getAllProductsByName("nonExistentKeyword").isEmpty());
    }


    @Test
    void deleteProduct_Deleted() {
        when(productRepository.findById(any())).thenReturn(Optional.of(savedProduct));
        doNothing().when(productRepository).deleteById(any());

        assertTrue(adminProductService.deleteProduct(1L));
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(productRepository).deleteById(any());

        assertFalse(adminProductService.deleteProduct(1L));
    }

    @Test
    void getProductById_Found() {
        savedProduct.setCategoryId(1L);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Test Category");

        when(productRepository.findById(any())).thenReturn(Optional.of(savedProduct));
        when(categoryClient.findById(1L)).thenReturn(categoryResponse);

        ProductDto result = adminProductService.getProductById(2L);

        assertNotNull(result);
        assertEquals(savedProduct.getId(), result.getId());
        assertEquals("Test Category", result.getCategoryName());
    }


    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        assertNull(adminProductService.getProductById(1L));
    }

    @Test
    void updateProduct() throws IOException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
        when(categoryClient.findById(1L)).thenReturn(categoryResponse);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productDto.setDescription("updatedDescription");

        ProductDto result = adminProductService.updateProduct(1L, productDto);

        assertEquals("updatedDescription", result.getDescription());
    }
}
