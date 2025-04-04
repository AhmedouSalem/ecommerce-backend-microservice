package com.aryan.reviewservice.service.customer.review;

import com.aryan.reviewservice.dto.*;
import com.aryan.reviewservice.feign.CartFeignClient;
import com.aryan.reviewservice.feign.OrderFeignClient;
import com.aryan.reviewservice.feign.ProductFeignClient;
import com.aryan.reviewservice.feign.UserFeignClient;
import com.aryan.reviewservice.model.Review;
import com.aryan.reviewservice.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private final OrderFeignClient orderFeignClient;
    @Autowired
    private final ProductFeignClient productFeignClient;
    @Autowired
    private final UserFeignClient userFeignClient;
    @Autowired
    private final ReviewRepository reviewRepository;
    @Autowired
    private final CartFeignClient cartFeignClient;

    public OrderedProductsResponseDto getOrderedProductsDetailsByOrderId(Long orderId) {
        ResponseEntity<OrderDto> optionalOrder = orderFeignClient.getOrderById(orderId);
        OrderedProductsResponseDto orderedProductsResponseDto = new OrderedProductsResponseDto();
        ResponseEntity<List<CartItemsDto>> cartItemsDtoList = cartFeignClient.getCartItemsByOrderId(orderId);

        if (optionalOrder.getBody() != null && optionalOrder.getStatusCode().is2xxSuccessful() && cartItemsDtoList.getStatusCode().is2xxSuccessful()) {
            orderedProductsResponseDto.setOrderAmount(optionalOrder.getBody().getAmount());


            optionalOrder.getBody().setCartItems(cartItemsDtoList.getBody());

            List<ProductDto> productDtoList = new ArrayList<>();
            for (CartItemsDto cartItems : optionalOrder.getBody().getCartItems()) {
                ProductDto productDto = ProductDto.builder()
                        .id(cartItems.getProductId())
                        .name(cartItems.getProductName())
                        .price(cartItems.getPrice())
                        .quantity(cartItems.getQuantity())
                        .byteImg(cartItems.getReturnedImage())
                        .build();

                productDtoList.add(productDto);
            }

            orderedProductsResponseDto.setProductDtoList(productDtoList);
        }
        log.info("Retrieved ordered products details for order ID: {}", orderId);
        return orderedProductsResponseDto;
    }

    @Transactional
    public ReviewDto giveReview(ReviewDto reviewDto) throws IOException {
        ResponseEntity<ProductDto> optionalProduct = productFeignClient.getProductById(reviewDto.getProductId());
        ResponseEntity<UserDto> optionalUser = userFeignClient.getUserById(reviewDto.getUserId());
        if (optionalUser.getStatusCode().is2xxSuccessful() && optionalUser.getBody() != null && optionalProduct.getBody() != null) {
            Review review = new Review();

            review.setDescription(reviewDto.getDescription());
            review.setRating(reviewDto.getRating());
            review.setUserId(optionalUser.getBody().getId());
            review.setProductId(optionalProduct.getBody().getId());
            review.setUser(optionalUser.getBody());
            review.setProduct(optionalProduct.getBody());
            log.info("Review details set for user ID: {} and product ID: {}", reviewDto.getUserId(), reviewDto.getProductId());

            if (reviewDto.getImg() != null) {
                review.setImg(reviewDto.getImg().getBytes());
            }

            return reviewRepository.save(review).getDto();
        }

        log.error("Failed to give review. Product or user not found.");
        return null;
    }
}
