package com.aryan.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    private String name;

    private Long price;

    private String description;

    private byte[] byteImg;

    private Long categoryId;

    private String categoryName;

    private MultipartFile img;

    private Long quantity;
}