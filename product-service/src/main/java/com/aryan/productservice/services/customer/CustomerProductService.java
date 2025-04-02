package com.aryan.productservice.services.customer;



import com.aryan.productservice.dto.ProductDetailDto;
import com.aryan.productservice.dto.ProductDto;

import java.util.List;

public interface CustomerProductService {
	List<ProductDto> getAllProducts();

	List<ProductDto> getAllProductsByName(String name);

	ProductDetailDto getProductDetailById(Long productId);

	ProductDto getProductById(Long productId);

}
