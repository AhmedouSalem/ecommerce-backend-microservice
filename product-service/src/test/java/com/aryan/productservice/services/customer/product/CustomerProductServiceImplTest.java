package com.aryan.productservice.services.customer.product;

import com.aryan.productservice.dto.*;
import com.aryan.productservice.feign.CategoryClient;
import com.aryan.productservice.feign.ReviewClient;
import com.aryan.productservice.model.FAQ;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.FAQRepository;
import com.aryan.productservice.repository.ProductRepository;
import com.aryan.productservice.services.customer.CustomerProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryClient categoryClient;

    @Mock
    private ReviewClient reviewClient;

    @Mock
    private FAQRepository faqRepository;

    @InjectMocks
    private CustomerProductServiceImpl customerProductService;

    private Product product;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .categoryId(10L)
                .price(100L)
                .description("Test Desc")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("demoCategory")
                .description("demoDescription")
                .build();

        customerProductService= new CustomerProductServiceImpl(productRepository, faqRepository, reviewClient);
        ReflectionTestUtils.setField(customerProductService,"categoryClient",categoryClient);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductDto() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(categoryClient.findById(10L)).thenReturn(categoryResponse);

        List<ProductDto> result = customerProductService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAll();
        verify(categoryClient).findById(10L);
    }

    @Test
    void getAllProductsByName_ShouldReturnMatchingProductDtos() {
        when(productRepository.findAllByNameContaining("Test")).thenReturn(List.of(product));
        when(categoryClient.findById(10L)).thenReturn(categoryResponse);

        List<ProductDto> result = customerProductService.getAllProductsByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAllByNameContaining("Test");
        verify(categoryClient).findById(10L);
    }

    @Test
    void getProductDetailById_ShouldReturnProductDetailDto_WhenProductExists() {
        // Préparation du produit
        product.setId(1L);
        product.setName("Test Product");
        product.setCategoryId(1L); // Important pour éviter que getCategoryId() retourne null

        // Préparation des mocks
        FAQ faq = FAQ.builder()
                .id(1L)
                .question("Q?")
                .answer("A.")
                .product(product)
                .build();

        ReviewDto reviewDto = new ReviewDto();
        ResponseEntity<List<ReviewDto>> reviewResponse = ResponseEntity.ok(List.of(reviewDto));

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Test Category");

        // Mocks
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(faqRepository.findAllByProductId(1L)).thenReturn(List.of(faq));
        when(reviewClient.findReviewByProductId(1L)).thenReturn(reviewResponse);
        when(categoryClient.findById(1L)).thenReturn(categoryResponse);

        // Exécution
        ProductDetailDto result = customerProductService.getProductDetailById(1L);

        // Vérifications
        assertNotNull(result);
        assertEquals("Test Product", result.getProductDto().getName());
        assertEquals("Test Category", result.getProductDto().getCategoryName());
        assertEquals(1, result.getFaqDtoList().size());
        assertEquals(1, result.getReviewDtoList().size());

        verify(productRepository).findById(1L);
        verify(faqRepository).findAllByProductId(1L);
        verify(reviewClient).findReviewByProductId(1L);
        verify(categoryClient).findById(1L);
    }



    @Test
    void getProductDetailById_ShouldReturnNull_WhenProductNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        ProductDetailDto result = customerProductService.getProductDetailById(2L);

        assertNull(result);
        verify(productRepository).findById(2L);
        verify(faqRepository, never()).findAllByProductId(anyLong());
    }

    @Test
    void getProductById_ShouldReturnProductDto_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryClient.findById(10L)).thenReturn(categoryResponse);

        ProductDto result = customerProductService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
        verify(categoryClient).findById(10L);
    }

    @Test
    void getProductById_ShouldReturnNull_WhenProductNotFound() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        ProductDto result = customerProductService.getProductById(5L);

        assertNull(result);
        verify(productRepository).findById(5L);
        verify(categoryClient, never()).findById(anyLong());
    }
}
