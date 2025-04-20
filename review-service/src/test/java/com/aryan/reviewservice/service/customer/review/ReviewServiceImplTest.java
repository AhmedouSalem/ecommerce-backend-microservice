package com.aryan.reviewservice.service.customer.review;

import com.aryan.reviewservice.dto.*;
import com.aryan.reviewservice.feign.CartFeignClient;
import com.aryan.reviewservice.feign.OrderFeignClient;
import com.aryan.reviewservice.feign.ProductFeignClient;
import com.aryan.reviewservice.feign.UserFeignClient;
import com.aryan.reviewservice.model.Review;
import com.aryan.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private OrderFeignClient orderFeignClient;
    @Mock
    private ProductFeignClient productFeignClient;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CartFeignClient cartFeignClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGiveReview_success() throws IOException {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;

        MockMultipartFile mockImg = new MockMultipartFile(
                "img", "image.jpg", "image/jpeg", "test-image-content".getBytes()
        );

        ReviewDto inputDto = ReviewDto.builder()
                .userId(userId)
                .productId(productId)
                .rating(5L)
                .description("Très bon produit")
                .img(mockImg)
                .build();

        ProductDto productDto = ProductDto.builder().id(productId).name("Produit A").build();
        UserDto userDto = UserDto.builder().id(userId).name("Ahmed").build();

        Review savedReview = Review.builder()
                .id(100L)
                .rating(5L)
                .description("Très bon produit")
                .img(mockImg.getBytes())
                .userId(userId)
                .productId(productId)
                .user(userDto)
                .product(productDto)
                .build();

        when(productFeignClient.getProductById(productId)).thenReturn(ResponseEntity.ok(productDto));
        when(userFeignClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // Act
        ReviewDto result = reviewService.giveReview(inputDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getUsername()).isEqualTo("Ahmed");
        assertThat(result.getProductName()).isEqualTo("Produit A");

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testGiveReview_userOrProductNotFound_shouldReturnNull() throws IOException {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;

        MockMultipartFile mockImg = new MockMultipartFile(
                "img", "image.jpg", "image/jpeg", "test-image-content".getBytes()
        );

        ReviewDto inputDto = ReviewDto.builder()
                .userId(userId)
                .productId(productId)
                .rating(4L)
                .description("Pas mal")
                .img(mockImg)
                .build();

        // Simuler que l'utilisateur existe mais que le produit est introuvable
        UserDto userDto = UserDto.builder().id(userId).name("Ahmed").build();

        when(userFeignClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));
        when(productFeignClient.getProductById(productId)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        // Act
        ReviewDto result = reviewService.giveReview(inputDto);

        // Assert
        assertThat(result).isNull();
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testGetOrderedProductsDetailsByOrderId_success() {
        // Arrange
        Long orderId = 1L;

        CartItemsDto cartItem = CartItemsDto.builder()
                .productId(10L)
                .productName("Produit Test")
                .price(100L)
                .quantity(2L)
                .returnedImage("image".getBytes())
                .build();

        List<CartItemsDto> cartItems = List.of(cartItem);

        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .amount(200L)
                .cartItems(cartItems)
                .build();

        ResponseEntity<OrderDto> orderResponse = ResponseEntity.ok(orderDto);
        ResponseEntity<List<CartItemsDto>> cartItemsResponse = ResponseEntity.ok(cartItems);

        when(orderFeignClient.getOrderById(orderId)).thenReturn(orderResponse);
        when(cartFeignClient.getCartItemsByOrderId(orderId)).thenReturn(cartItemsResponse);

        // Act
        OrderedProductsResponseDto result = reviewService.getOrderedProductsDetailsByOrderId(orderId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderAmount()).isEqualTo(200L);
        assertThat(result.getProductDtoList()).hasSize(1);

        ProductDto product = result.getProductDtoList().get(0);
        assertThat(product.getId()).isEqualTo(10L);
        assertThat(product.getName()).isEqualTo("Produit Test");
        assertThat(product.getPrice()).isEqualTo(100L);
        assertThat(product.getQuantity()).isEqualTo(2L);
        assertThat(product.getByteImg()).isEqualTo("image".getBytes());

        verify(orderFeignClient, times(1)).getOrderById(orderId);
        verify(cartFeignClient, times(1)).getCartItemsByOrderId(orderId);
    }

    @Test
    void testGetOrderedProductsDetailsByOrderId_orderNotFound_shouldReturnEmptyResponse() {
        // Arrange
        Long orderId = 999L;

        // Simuler une réponse vide ou avec un status 404
        ResponseEntity<OrderDto> orderResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        ResponseEntity<List<CartItemsDto>> cartItemsResponse = ResponseEntity.ok(List.of()); // même si la commande échoue, on suppose que les cartItems sont retournés OK

        when(orderFeignClient.getOrderById(orderId)).thenReturn(orderResponse);
        when(cartFeignClient.getCartItemsByOrderId(orderId)).thenReturn(cartItemsResponse);

        // Act
        OrderedProductsResponseDto result = reviewService.getOrderedProductsDetailsByOrderId(orderId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderAmount()).isNull();
        assertThat(result.getProductDtoList()).isNull(); // dans ton code, cette liste n’est initialisée que si l’ordre est OK

        verify(orderFeignClient, times(1)).getOrderById(orderId);
        verify(cartFeignClient, times(1)).getCartItemsByOrderId(orderId);
    }

}
