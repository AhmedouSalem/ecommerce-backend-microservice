package com.aryan.cartservice.feign;

import com.aryan.cartservice.config.FeignClientInterceptor;
import com.aryan.cartservice.dto.OrderDto;
import com.aryan.cartservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "product-service", configuration = FeignClientInterceptor.class)
public interface ProductFeignClient {
    @GetMapping("/api/customer/product/microservice/{productId}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("productId") Long productId);
}
