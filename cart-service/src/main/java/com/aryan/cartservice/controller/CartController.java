package com.aryan.cartservice.controller;


import com.aryan.cartservice.dto.*;
import com.aryan.cartservice.exceptions.ValidationException;
import com.aryan.cartservice.service.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	private final CartService cartService;

	@PostMapping("/cart")
	public ResponseEntity<?> addProductToCart(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to add product to cart for user with ID: {}", addProductInCartDto.getUserId());
		return cartService.addProductToCart(addProductInCartDto);
	}

	@GetMapping("/cart/{userId}")
	public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
		log.info("Received request to get cart for user with ID: {}", userId);
		OrderDto orderDto = cartService.getCartByUserId(userId);
		if (orderDto == null) {
			log.error("No active order found for user with ID: {}", userId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("Returned order DTO: {}", orderDto);
		return ResponseEntity.status(HttpStatus.OK).body(orderDto);
	}

	@GetMapping("/coupon/{userId}/{code}")
	public ResponseEntity<?> applyCoupon(@PathVariable Long userId, @PathVariable String code) {
		log.info("Received request to apply coupon '{}' for user with ID: {}", code, userId);
		try {
			OrderDto orderDto = cartService.applyCoupon(userId, code);
			return ResponseEntity.ok(orderDto);
		} catch (ValidationException e) {
			log.warn("Failed to apply coupon '{}' for user with ID: {}", code, userId, e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/addition")
	public ResponseEntity<OrderDto> increaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to increase quantity of product '{}' for user with ID: {}", addProductInCartDto.getProductId(), addProductInCartDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.increaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/deduction")
	public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
		log.info("Received request to decrease quantity of product '{}' for user with ID: {}", addProductInCartDto.getProductId(), addProductInCartDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.decreaseProductQuantity(addProductInCartDto));
	}

	@PostMapping("/placedOrder")
	public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderDto placeOrderDto) {
		log.info("Received request to place order for user with ID: {}", placeOrderDto.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.placedOrder(placeOrderDto));
	}

	@GetMapping("/myOrders/{userId}")
	public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId){
		log.info("Received request to get placed orders for user with ID: {}", userId);
		return ResponseEntity.ok(cartService.getMyPlacedOrders(userId));
	}

	@GetMapping("/command/tracking/order/{trackingId}")
	public ResponseEntity<OrderDto> searchOrderByTrackingId(@PathVariable UUID trackingId) {
		log.info("Received request to search order by tracking ID: {}", trackingId);
		OrderDto orderDto = cartService.searchOrderByTrackingId(trackingId);
		if (orderDto == null) {
			log.warn("Order not found for tracking ID: {}", trackingId);
			return ResponseEntity.notFound().build();
		}
		log.info("Found order for tracking ID: {}", trackingId);
		return ResponseEntity.ok(orderDto);
	}

	@GetMapping("/microservice/cart/{orderId}")
	public ResponseEntity<List<CartItemsDto>> getCartItemsByOrderId(@PathVariable Long orderId) {
		log.info("Received request to get cart items by order ID: {}", orderId);
		List<CartItemsDto> cartItemsDtoList = cartService.getCartItemsByOrderId(orderId);
		if (cartItemsDtoList == null) {
			log.warn("Cart items not found for order ID: {}", orderId);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cartItemsDtoList);
	}

}