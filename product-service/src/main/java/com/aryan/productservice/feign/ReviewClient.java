package com.aryan.productservice.feign;

import com.aryan.productservice.config.FeignClientInterceptor;
import com.aryan.productservice.dto.OrderedProductsResponseDto;
import com.aryan.productservice.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "review-service",
        configuration = FeignClientInterceptor.class)
public interface ReviewClient {
    @GetMapping("/api/customer/reviews/{productId}")
    ResponseEntity<List<ReviewDto>> findReviewByProductId(@PathVariable Long productId);
}
