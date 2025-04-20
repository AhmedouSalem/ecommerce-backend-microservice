package com.aryan.reviewservice.repository;

import com.aryan.reviewservice.dto.ProductDto;
import com.aryan.reviewservice.dto.UserDto;
import com.aryan.reviewservice.model.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("Should save and retrieve a review by ID")
    void testSaveAndFindById() {
        // Given
        Review review = Review.builder()
                .rating(4L)
                .description("Produit sympa")
                .userId(1L)
                .productId(2L)
                .user(UserDto.builder().id(1L).name("Ahmed").build())
                .product(ProductDto.builder().id(2L).name("Produit A").build())
                .build();

        // When
        Review saved = reviewRepository.save(review);
        Optional<Review> result = reviewRepository.findById(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Produit sympa");
    }

    @Test
    @DisplayName("Should find all reviews by product ID")
    void testFindAllByProductId() {
        // Given
        Review review1 = Review.builder()
                .rating(5L)
                .description("Excellent produit")
                .productId(100L)
                .userId(1L)
                .build();

        Review review2 = Review.builder()
                .rating(3L)
                .description("Pas mal")
                .productId(100L)
                .userId(2L)
                .build();

        Review review3 = Review.builder()
                .rating(1L)
                .description("Nul")
                .productId(200L)
                .userId(3L)
                .build();

        reviewRepository.saveAll(List.of(review1, review2, review3));

        // When
        List<Review> reviewsForProduct100 = reviewRepository.findAllByProductId(100L);

        // Then
        assertThat(reviewsForProduct100).hasSize(2);
        assertThat(reviewsForProduct100).extracting("description")
                .containsExactlyInAnyOrder("Excellent produit", "Pas mal");
    }
}
