package com.starbux.order.controller;

import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ProductRequest;
import com.starbux.order.service.ProductService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/v1/product")
@Api(description = "This Product API provides to create, update, delete product and gets a product by product id and all products.")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ApiOperation(value = "This endpoint is for creating Product",
            produces = "Application/JSON",
            response = ProductDto.class,
            httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully product created"),
            @ApiResponse(code = 400, message = "Invalid product request")})
    public ResponseEntity<ProductDto> createProduct(
            @ApiParam(value = "Product creates by ProductRequest json object. ProductRequest should be valid",
                    required = true,
                    type = "ProductRequest")
            @Valid @RequestBody ProductRequest productRequest) {
        logger.info("Product is creating");
        ProductDto productDto = productService.createProduct(productRequest);
        Link productLink = linkTo(methodOn(ProductController.class).getProduct(productDto.getProductId())).withSelfRel();
        Link allProductsLink = linkTo(methodOn(ProductController.class).getAllProducts()).withRel("allProduct");
        productDto.add(productLink, allProductsLink);
        logger.info("Product created with the following product id:{}", productDto.getProductId());
        return ResponseEntity.ok(productDto);
    }

    @PutMapping("/{productId}")
    @ApiOperation(value = "This endpoint is for updating Product with product Id",
            produces = "Application/JSON",
            response = ProductDto.class,
            httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully product updated"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 400, message = "Invalid product request")})
    public ResponseEntity<ProductDto> updateProduct(
            @ApiParam(value = "Product updates by valid product id.",
                    required = true,
                    type = "String")
            @PathVariable("productId") String productId,
            @ApiParam(value = "Product updates by ProductRequest json object. ProductRequest should be valid",
                    required = true,
                    type = "ProductRequest")
            @Valid @RequestBody ProductRequest productRequest) {
        logger.info("Product is updating");
        ProductDto productDto = productService.updateProduct(productId, productRequest);
        Link productLink = linkTo(methodOn(ProductController.class).getProduct(productDto.getProductId())).withSelfRel();
        productDto.add(productLink);
        logger.info("Product updated by the following product id: {}", productDto.getProductId());
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("/{productId}")
    @ApiOperation(value = "This endpoint is for deleting Product with product Id",
            produces = "Application/JSON",
            response = ProductDto.class,
            httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully product deleted"),
            @ApiResponse(code = 404, message = "Product not found")})
    public ResponseEntity<Void> deleteProduct(
            @ApiParam(value = "Product can be deleted by valid product id",
                    required = true,
                    type = "String")
            @PathVariable String productId) {
        logger.info("Product is deleting");
        productService.deleteProduct(productId);
        logger.info("Product deleted by the following product id: {}", productId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @GetMapping
    @ApiOperation(value = "This endpoint is for getting All Products from Repository",
            produces = "Application/JSON",
            response = ProductDto.class,
            httpMethod = "GET")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        logger.info("All products are fetching");
        List<ProductDto> productDtoList = productService.getAllProducts();
        productDtoList.forEach(productDto -> productDto.add(linkTo(methodOn(ProductController.class).getProduct(productDto.getProductId())).withSelfRel()));
        return ResponseEntity.ok(productDtoList);
    }

    @GetMapping("/{productId}")
    @ApiOperation(value = "This endpoint is for getting Product by product id",
            produces = "Application/JSON",
            response = ProductDto.class,
            httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product fetched successfully"),
            @ApiResponse(code = 404, message = "Product not found")})
    public ResponseEntity<ProductDto> getProduct(
            @ApiParam(value = "An product fetches by valid product id",
                    required = true,
                    type = "String")
            @PathVariable("productId") String productId) {
        logger.info("Product is fetching by following product id: {}", productId);
        return ResponseEntity.ok(productService.getProductById(productId));
    }

}
