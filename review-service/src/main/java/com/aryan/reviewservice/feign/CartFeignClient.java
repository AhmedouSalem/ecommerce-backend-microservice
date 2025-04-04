package com.aryan.reviewservice.feign;

import com.aryan.reviewservice.config.FeignClientInterceptor;
import com.aryan.reviewservice.dto.CartItemsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cart-service", configuration = FeignClientInterceptor.class)
public interface CartFeignClient {
    @GetMapping("/api/customer/microservice/cart/{orderId}")
    ResponseEntity<List<CartItemsDto>> getCartItemsByOrderId(@PathVariable Long orderId);
}
