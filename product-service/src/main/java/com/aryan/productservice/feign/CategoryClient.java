package com.aryan.productservice.feign;

import com.aryan.productservice.config.FeignClientInterceptor;
import com.aryan.productservice.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "category-service",
        configuration = FeignClientInterceptor.class)
public interface CategoryClient {

    @GetMapping("/api/admin/categories")
    ResponseEntity<List<CategoryResponse>> getCategories();

    @GetMapping("/api/admin/category/{id}")
    CategoryResponse findById(@PathVariable("id") Long id);


}
