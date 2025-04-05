package com.aryan.productservice.services.admin;


import com.aryan.productservice.dto.CategoryResponse;
import com.aryan.productservice.dto.ProductDto;
import com.aryan.productservice.feign.CategoryClient;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductServiceImpl implements AdminProductService {
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final CategoryClient categoryClient;

    public ProductDto addProduct(ProductDto productDto) throws Exception {
        log.info("Adding a new product: {}", productDto.getName());
        CategoryResponse category = categoryClient.findById(productDto.getCategoryId());

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .img(productDto.getImg().getBytes())
                .categoryId(category.getId())
                .category(category) // Pas nécessaire
                .build();

        Product savedProduct = productRepository.save(product);

        return savedProduct.getDto();
    }

    public List<ProductDto> getAllProducts() {
        log.info("Fetching all products.");
        List<Product> products = productRepository.findAll();
        /** Récupérer les données de catégorie de cahque produits **/
        products.forEach(product -> {
            product.setCategory(categoryClient.findById(product.getCategoryId()));
        });
        /***  ***************************************************** ***/
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsByName(String name) {
        log.info("Fetching all products by name: {}", name);
        List<Product> products = productRepository.findAllByNameContaining(name);
        /** Récupérer les données de catégorie de cahque produits **/
        products.forEach(product -> {
            product.setCategory(categoryClient.findById(product.getCategoryId()));
        });
        /***  ***************************************************** ***/
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public boolean deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ProductDto getProductById(Long productId) {
        log.info("Fetching product with ID: {}", productId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        /**************************************************************************/
        optionalProduct.ifPresent(product -> {
            product.setCategory(categoryClient.findById(product.getCategoryId()));
        });
        /*************************************************************************/
        return optionalProduct.map(Product::getDto).orElse(null);
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        log.info("Updating product with ID: {}", productId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Optional<CategoryResponse> optionalCategory = Optional.ofNullable(categoryClient.findById(productDto.getCategoryId()));

        if (optionalProduct.isPresent() && optionalCategory.isPresent()) {
            Product product = optionalProduct.get().toBuilder()
                    .name(productDto.getName())
                    .price(productDto.getPrice())
                    .description(productDto.getDescription())
                    .categoryId(productDto.getCategoryId())
                    .category(optionalCategory.get())
                    .img(productDto.getByteImg() != null ? productDto.getByteImg() : optionalProduct.get().getImg())
                    .build();
            Product savedProduct = productRepository.save(product);
            savedProduct.setCategory(categoryClient.findById(product.getCategoryId()));
            return savedProduct.getDto();
        }
        return null;
    }
}
