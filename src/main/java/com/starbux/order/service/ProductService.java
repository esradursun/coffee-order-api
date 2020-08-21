package com.starbux.order.service;

import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ProductDtoConverter;
import com.starbux.order.dto.ProductRequest;
import com.starbux.order.exception.InvalidProductRequestException;
import com.starbux.order.exception.ProductNotFoundException;
import com.starbux.order.model.Product;
import com.starbux.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductDtoConverter productDtoConverter;

    public ProductService(ProductRepository productRepository, ProductDtoConverter productDtoConverter) {
        this.productRepository = productRepository;
        this.productDtoConverter = productDtoConverter;
    }

    public ProductDto createProduct(final ProductRequest productRequest) {
        if (!validateProductRequest(productRequest)) {
            logger.warn("Product request validation failed {}", productRequest);
            throw new InvalidProductRequestException("Product request validation failed");
        }

        Product product = Product.builder().name(productRequest.getName())
                .price(productRequest.getPrice())
                .productType(productRequest.getProductType())
                .build();
        productRepository.save(product);
        logger.info("Product is created by following id: {}", product.getId());
        return productDtoConverter.convert(product);
    }

    public ProductDto updateProduct(final String productId, final ProductRequest productRequest) {
        if (!validateProductRequest(productRequest)) {
            logger.warn("Product request validation failed {}", productRequest);
            throw new InvalidProductRequestException("Product request validation failed");
        }

        Product product = findProductById(productId);
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setProductType(productRequest.getProductType());
        productRepository.save(product);
        logger.info("Product is updated by following id: {}", product.getId());
        return productDtoConverter.convert(product);

    }

    private Boolean validateProductRequest(ProductRequest productRequest) {
        return !productRequest.getName().isEmpty() &&
                productRequest.getPrice() != null &&
                productRequest.getPrice() >= 0.0 &&
                productRequest.getProductType() != null;
    }

    private Product findProductById(String productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            logger.warn("Product could not found by productId: {}", productId);
            return new ProductNotFoundException("Product not found by following productId: " + productId);
        });
    }

    public void deleteProduct(final String productId) {
        logger.info("Product is deleted by following id: {}", productId);
        productRepository.delete(findProductById(productId));
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(productDtoConverter::convert).collect(Collectors.toList());
    }

    public ProductDto getProductById(String productId) {
        return productDtoConverter.convert(findProductById(productId));
    }
}
