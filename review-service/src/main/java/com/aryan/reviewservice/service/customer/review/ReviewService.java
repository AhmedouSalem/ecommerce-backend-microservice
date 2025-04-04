package com.aryan.reviewservice.service.customer.review;


import com.aryan.reviewservice.dto.OrderedProductsResponseDto;
import com.aryan.reviewservice.dto.ReviewDto;

import java.io.IOException;

public interface ReviewService {
	OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId);
	
	ReviewDto giveReview(ReviewDto reviewDto) throws IOException ;
}
