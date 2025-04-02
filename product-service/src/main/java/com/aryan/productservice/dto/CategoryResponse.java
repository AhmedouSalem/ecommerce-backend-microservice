package com.aryan.productservice.dto;

import lombok.*;

import java.util.Optional;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private String description;

    public Optional<Object> findById(Long categoryId) {
        return null;
    }
}
