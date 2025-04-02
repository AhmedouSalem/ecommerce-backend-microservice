package com.aryan.analyticsservice.model;

import com.aryan.analyticsservice.dto.FAQDto;
import com.aryan.analyticsservice.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String answer;

    private Long productId;

    @Transient
    private ProductDto product;

    public FAQDto getFAQDto() {
        return  FAQDto.builder()
                .id(id)
                .question(question)
                .answer(answer)
                .productId(product.getId())
                .build();

    }
}