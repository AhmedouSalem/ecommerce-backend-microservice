package com.aryan.reviewservice.service.customer.review;


import com.aryan.reviewservice.dto.OrderedProductsResponseDto;
import com.aryan.reviewservice.dto.ReviewDto;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
	OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId);
	
	ReviewDto giveReview(ReviewDto reviewDto) throws IOException ;

	// AJOUTER UNE FONCTION DE RECHERCHE PAR ID DE PRODUIT 20 Avril 2025
	List<ReviewDto> findReviewByProductId(Long productId);
}
