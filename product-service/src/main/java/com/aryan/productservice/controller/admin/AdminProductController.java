package com.aryan.productservice.controller.admin;


import java.util.List;

import com.aryan.productservice.dto.FAQDto;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.services.admin.AdminProductService;
import com.aryan.productservice.services.admin.faq.FAQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

	@Autowired
	private final AdminProductService adminProductService;
	private final FAQService faqService;

	@PostMapping("/product")
	public ResponseEntity<ProductDto> addProduct(@ModelAttribute ProductDto productDto) throws Exception {
		log.info("Received request to add a product with name: {}", productDto.getName());
		log.info("Received request to add a product with categoryID: {}", productDto.getCategoryId());
		ProductDto productDto1 = adminProductService.addProduct(productDto);
		log.info("Product added with ID: {}, and categoryId {}", productDto1.getId(), productDto1.getCategoryId());
		return ResponseEntity.status(HttpStatus.CREATED).body(productDto1);
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductDto>> getAllProduct() {
		log.info("Received request to get all products");
		List<ProductDto> productDtos = adminProductService.getAllProducts();
		log.info("Returning {} products", productDtos.size());
		ProductDto productDto = productDtos.get(0);
		log.info("Retourned product : [{}]", productDto);
		return ResponseEntity.ok(productDtos);
	}

	@GetMapping("/search/{name}")
	public ResponseEntity<List<ProductDto>> getAllProductByName(@PathVariable String name) {
		log.info("Received request to get products by name: {}", name);
		List<ProductDto> productDtos = adminProductService.getAllProductsByName(name);
		log.info("Returning {} products with name: {}", productDtos.size(), name);
		return ResponseEntity.ok(productDtos);
	}

	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
		log.info("Received request to delete product with ID: {}", productId);
		boolean deleted = adminProductService.deleteProduct(productId);
		if (deleted) {
			log.info("Product with ID: {} deleted successfully", productId);
			return ResponseEntity.noContent().build();
		} else {
			log.warn("Product with ID: {} not found", productId);
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/faq/{productId}")
	public ResponseEntity<FAQDto> postFAQ(@PathVariable Long productId, @RequestBody FAQDto faqDto) {
		log.info("Received request to add FAQ for product with ID: {}", productId);
		FAQDto createdFAQ = faqService.postFAQ(productId, faqDto);
		log.info("FAQ added for product with ID: {}", productId);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdFAQ);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
		log.info("Received request to get product by ID: {}", productId);
		ProductDto productDto = adminProductService.getProductById(productId);
		if (productDto != null) {
			log.info("Returning product with ID: {}", productId);
			return ResponseEntity.ok(productDto);
		} else {
			log.warn("Product with ID: {} not found", productId);
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/product/{productId}")
	public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId, @ModelAttribute ProductDto productDto) throws IOException {
		log.info("Received request to update product with ID: {}", productId);
		ProductDto updatedProduct = adminProductService.updateProduct(productId, productDto);
		if (updatedProduct != null) {
			log.info("Product with ID: {} updated successfully", productId);
			return ResponseEntity.ok(updatedProduct);
		} else {
			log.warn("Product with ID: {} not found for update", productId);
			return ResponseEntity.notFound().build();
		}
	}
}
