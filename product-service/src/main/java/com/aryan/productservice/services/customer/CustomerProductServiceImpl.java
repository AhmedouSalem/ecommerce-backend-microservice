package com.aryan.productservice.services.customer;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aryan.productservice.dto.CategoryResponse;
import com.aryan.productservice.dto.ProductDetailDto;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.model.CategoryClient;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProductServiceImpl implements CustomerProductService {
	@Autowired
	private final ProductRepository productRepository;
	@Autowired
	private CategoryClient categoryClient;
//	private final FAQRepository faqRepository;
//	private final ReviewRepository reviewRepository;

	public List<ProductDto> getAllProducts() {
		log.info("Fetching all products");
		List<Product> products = productRepository.findAll();
		/** Récupérer les données de catégorie de cahque produits **/
		products.forEach(product -> {
			product.setCategory(categoryClient.findById(product.getCategoryId()));
		});
		/***  ***************************************************** ***/
		return products.stream().map(Product::getDto).collect(Collectors.toList());
	}

	public List<ProductDto> getAllProductsByName(String name) {
		log.info("Fetching products by name containing '{}'", name);
		List<Product> products = productRepository.findAllByNameContaining(name);
		/** Récupérer les données de catégorie de cahque produits **/
		products.forEach(product -> {
			product.setCategory(categoryClient.findById(product.getCategoryId()));
		});
		/***  ***************************************************** ***/
		return products.stream().map(Product::getDto).collect(Collectors.toList());
	}

	public ProductDetailDto getProductDetailById(Long productId) {
//		log.info("Fetching product details for product with ID '{}'", productId);
//		Optional<Product> optionalProduct = productRepository.findById(productId);
//		if (optionalProduct.isPresent()) {
//			List<FAQ> faqs = faqRepository.findAllByProductId(productId);
//			List<Review> reviews = reviewRepository.findAllByProductId(productId);
//
//			ProductDetailDto productDetailDto = new ProductDetailDto();
//			productDetailDto.setProductDto(optionalProduct.get().getDto());
//			productDetailDto.setFaqDtoList(faqs.stream().map(FAQ::getFAQDto).collect(Collectors.toList()));
//			productDetailDto.setReviewDtoList(reviews.stream().map(Review::getDto).collect(Collectors.toList()));
//
//			return productDetailDto;
//		}
//		log.error("Product with ID '{}' not found", productId);
		return null;
	}

	@Override
	public ProductDto getProductById(Long productId) {
		log.info("Fetching product by id {}", productId);
		Optional<Product> product =  productRepository.findById(productId);
		if (product.isPresent()) {
			log.info("Fetching product by id {}", productId);
			CategoryResponse categoryDto = categoryClient.findById(product.get().getCategoryId());
			product.get().setCategory(categoryDto);
			return product.get().getDto();
		}
		return null;
	}
}
