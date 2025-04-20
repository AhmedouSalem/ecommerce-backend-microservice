package com.aryan.reviewservice.controller.customer;

import com.aryan.reviewservice.dto.OrderedProductsResponseDto;
import com.aryan.reviewservice.dto.ProductDto;
import com.aryan.reviewservice.dto.ReviewDto;
import com.aryan.reviewservice.service.customer.review.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @JsonIgnore
    private MultipartFile img;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOrderedProductDetailsByOrderId_success() throws Exception {
        Long orderId = 1L;

        ProductDto product = ProductDto.builder()
                .id(10L)
                .name("Produit Test")
                .price(100L)
                .quantity(2L)
                .build();

        OrderedProductsResponseDto responseDto = new OrderedProductsResponseDto();
        responseDto.setOrderAmount(200L);
        responseDto.setProductDtoList(List.of(product));

        when(reviewService.getOrderedProductsDetailsByOrderId(orderId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/customer/ordered-products/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderAmount").value(200))
                .andExpect(jsonPath("$.productDtoList[0].name").value("Produit Test"));
    }

    @Test
    void testGiveReview_success_statusOnly() throws Exception {
        MockMultipartFile img = new MockMultipartFile(
                "img", "image.jpg", "image/jpeg", "mock image content".getBytes()
        );

        ReviewDto mockResponse = ReviewDto.builder()
                .id(10L)
                .productId(2L)
                .userId(1L)
                .description("Super produit")
                .rating(5L)
                .username("Ahmed")
                .productName("Produit A")
                .build();

        when(reviewService.giveReview(Mockito.any())).thenReturn(mockResponse);

        mockMvc.perform(
                        multipart("/api/customer/review")
                                .file(img)
                                .param("productId", "2")
                                .param("userId", "1")
                                .param("description", "Super produit")
                                .param("rating", "5")
                )
                .andExpect(status().isCreated());
    }

    @Test
    void testGetOrderedProductDetailsByOrderId_notFound() throws Exception {
        Long orderId = 999L;

        when(reviewService.getOrderedProductsDetailsByOrderId(orderId)).thenReturn(new OrderedProductsResponseDto());

        mockMvc.perform(get("/api/customer/ordered-products/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderAmount").doesNotExist())
                .andExpect(jsonPath("$.productDtoList").doesNotExist()); // ou isEmpty si liste vide
    }

    @Test
    void testGiveReview_failure_shouldReturnBadRequest() throws Exception {
        MockMultipartFile img = new MockMultipartFile(
                "img", "image.jpg", "image/jpeg", "fake".getBytes()
        );

        when(reviewService.giveReview(Mockito.any())).thenReturn(null);

        mockMvc.perform(
                        multipart("/api/customer/review")
                                .file(img)
                                .param("productId", "2")
                                .param("userId", "1")
                                .param("description", "Échec")
                                .param("rating", "1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Something went wrong"));
    }


}
