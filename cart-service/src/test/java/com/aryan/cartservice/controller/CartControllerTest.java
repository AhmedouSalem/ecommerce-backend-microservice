package com.aryan.cartservice.controller;

import com.aryan.cartservice.dto.AddProductInCartDto;
import com.aryan.cartservice.dto.OrderDto;
import com.aryan.cartservice.service.customer.cart.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = com.aryan.cartservice.config.SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void testGetCartByUserId_shouldReturn200() throws Exception {
        OrderDto order = OrderDto.builder()
                .id(1L)
                .userId(1L)
                .amount(100L)
                .build();

        when(cartService.getCartByUserId(1L)).thenReturn(order);

        mockMvc.perform(get("/api/customer/cart/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddProductToCart_shouldReturn201() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "productId": 10
                }
                """;

        when(cartService.addProductToCart(any()))
                .thenReturn(new org.springframework.http.ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/api/customer/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testPlaceOrder_shouldReturn201() throws Exception {
        String json = """
                {
                    "userId": 1,
                    "orderDescription": "Test",
                    "address": "123 rue"
                }
                """;

        when(cartService.placedOrder(any()))
                .thenReturn(OrderDto.builder().id(100L).userId(1L).build());

        mockMvc.perform(post("/api/customer/placedOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testApplyCoupon_shouldReturn200() throws Exception {
        when(cartService.applyCoupon(1L, "SAVE10"))
                .thenReturn(OrderDto.builder().id(1L).userId(1L).build());

        mockMvc.perform(get("/api/customer/coupon/1/SAVE10"))
                .andExpect(status().isOk());
    }

    @Test
    void testIncreaseQuantity_shouldReturn201() throws Exception {
        String json = """
            {
                "userId": 1,
                "productId": 10
            }
            """;

        when(cartService.increaseProductQuantity(any()))
                .thenReturn(OrderDto.builder().id(1L).userId(1L).build());

        mockMvc.perform(post("/api/customer/addition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testDecreaseQuantity_shouldReturn201() throws Exception {
        String json = """
            {
                "userId": 1,
                "productId": 10
            }
            """;

        when(cartService.decreaseProductQuantity(any()))
                .thenReturn(OrderDto.builder().id(1L).userId(1L).build());

        mockMvc.perform(post("/api/customer/deduction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetMyPlacedOrders_shouldReturn200() throws Exception {
        when(cartService.getMyPlacedOrders(1L))
                .thenReturn(List.of(OrderDto.builder().id(101L).build()));

        mockMvc.perform(get("/api/customer/myOrders/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchOrderByTracking_shouldReturn200() throws Exception {
        UUID trackingId = UUID.randomUUID();
        when(cartService.searchOrderByTrackingId(trackingId))
                .thenReturn(OrderDto.builder().id(1L).trackingId(trackingId).build());

        mockMvc.perform(get("/api/customer/command/tracking/order/" + trackingId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCartItemsByOrderId_shouldReturn200() throws Exception {
        when(cartService.getCartItemsByOrderId(200L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/customer/microservice/cart/200"))
                .andExpect(status().isOk());
    }

}
