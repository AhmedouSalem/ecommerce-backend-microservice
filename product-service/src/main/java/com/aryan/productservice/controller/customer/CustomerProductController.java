package com.aryan.productservice.controller.customer;


import com.aryan.productservice.dto.ProductDetailDto;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.ProductRepository;
import com.aryan.productservice.services.customer.CustomerProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerProductController {

    private final CustomerProductService customerProductService;
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProduct() {
        log.info("Received request to get all products");
        List<ProductDto> productDtos = customerProductService.getAllProducts();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductDto>> getAllProductByName(@PathVariable String name) {
        log.info("Received request to search products by name: {}", name);
        List<ProductDto> productDtos = customerProductService.getAllProductsByName(name);
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetailById(@PathVariable Long productId) {
        log.info("Received request to get product detail for product with ID: {}", productId);
        ProductDetailDto productDetailDto = customerProductService.getProductDetailById(productId);
        if (productDetailDto == null) {
            log.warn("Product detail not found for product with ID: {}", productId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productDetailDto);
    }

    @GetMapping("/product/microservice/{productId}")
    public ResponseEntity<ProductDto> getProductByProductId(@PathVariable Long productId) {
        log.info("Received request to get product detail for product with ID: {}", productId);
        ProductDto productDto = customerProductService.getProductById(productId);
        if (productDto == null) {
            log.warn("Product detail not found for product with ID: {}", productId);
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(productDto);
        }
    }
}