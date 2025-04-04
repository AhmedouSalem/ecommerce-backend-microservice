package com.aryan.reviewservice.model;

import com.aryan.reviewservice.dto.ProductDto;
import com.aryan.reviewservice.dto.ReviewDto;
import com.aryan.reviewservice.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rating;

    @Lob
    private String description;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] img;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long userId;
    @Transient
    private UserDto user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long productId;
    @Transient
    private ProductDto product;


    public ReviewDto getDto() {
        return ReviewDto.builder()
                .id(id)
                .rating(rating)
                .description(description)
                .returnedImg(img)
                .productId(product.getId())
                .userId(user.getId())
                .username(user.getName())
                .productName(product.getName())
                .build();
    }


}