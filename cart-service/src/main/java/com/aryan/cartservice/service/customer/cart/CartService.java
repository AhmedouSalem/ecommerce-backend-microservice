package com.aryan.cartservice.service.customer.cart;


import com.aryan.cartservice.dto.AddProductInCartDto;
import com.aryan.cartservice.dto.OrderDto;
import com.aryan.cartservice.dto.PlaceOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CartService {
	ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto);

	OrderDto getCartByUserId(Long userId);
	
	OrderDto applyCoupon(Long userId,String code);
	
	OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto);
	
	OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto);
	
	OrderDto placedOrder(PlaceOrderDto placeOrderDto);
	
	List<OrderDto> getMyPlacedOrders(Long userId);
	
	OrderDto searchOrderByTrackingId(UUID trackingId);
}
