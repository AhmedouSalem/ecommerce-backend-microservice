package com.aryan.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;


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

	@Override
	public String toString() {
		return "ProductDto{" +
				"id=" + id +
				", name='" + name + '\'' +
				", price=" + price +
				", description='" + description + '\'' +
				", byteImg=" + Arrays.toString(byteImg) +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", img=" + img +
				", quantity=" + quantity +
				'}';
	}
}
