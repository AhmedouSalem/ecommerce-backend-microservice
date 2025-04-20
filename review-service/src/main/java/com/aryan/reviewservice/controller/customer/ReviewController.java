package com.aryan.reviewservice.controller.customer;

import com.aryan.reviewservice.dto.OrderedProductsResponseDto;
import com.aryan.reviewservice.dto.ReviewDto;
import com.aryan.reviewservice.service.customer.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
@Slf4j
public class ReviewController {
	private final ReviewService reviewService;

	@GetMapping("/ordered-products/{orderId}")
	public ResponseEntity<OrderedProductsResponseDto> getOrderedProductDetailsByOrderId(@PathVariable Long orderId) {
		log.info("Received request to get ordered product details for order with ID: {}", orderId);
		OrderedProductsResponseDto responseDto = reviewService.getOrderedProductsDetailsByOrderId(orderId);
		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/review")
	public ResponseEntity<?> giveReview(@ModelAttribute ReviewDto reviewDto) throws IOException {
		log.info("Received request to submit review for product with ID: {}", reviewDto.getProductId());
		ReviewDto submittedReview = reviewService.giveReview(reviewDto);
		if (submittedReview == null) {
			log.warn("Failed to submit review for product with ID: {}", reviewDto.getProductId());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
		}
		log.info("Review submitted successfully for product with ID: {}", reviewDto.getProductId());
		return ResponseEntity.status(HttpStatus.CREATED).body(submittedReview);
	}

	// AJOUTER UNE FONCTION DE RECHERCHE PAR ID DE PRODUIT 20 Avril 2025
	@GetMapping("/reviews/{productId}")
	public ResponseEntity<List<ReviewDto>> findReviewByProductId(@PathVariable Long productId) {
		log.info("Received request to find reviews for product with ID: {}", productId);
		List<ReviewDto> reviews = reviewService.findReviewByProductId(productId);
		if (reviews == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(reviews);
	}
}
