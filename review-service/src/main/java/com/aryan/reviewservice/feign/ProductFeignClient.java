package com.aryan.reviewservice.feign;


import com.aryan.reviewservice.config.FeignClientInterceptor;
import com.aryan.reviewservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", configuration = FeignClientInterceptor.class)
public interface ProductFeignClient {
    @GetMapping("/api/customer/product/microservice/{productId}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("productId") Long productId);
}
