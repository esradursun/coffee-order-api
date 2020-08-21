package com.starbux.order.service;

import com.starbux.order.TestSupport;
import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ProductRequest;
import com.starbux.order.dto.ProductDtoConverter;
import com.starbux.order.exception.ProductNotFoundException;
import com.starbux.order.model.Product;
import com.starbux.order.model.ProductType;
import com.starbux.order.repository.ProductRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductServiceTest extends TestSupport{

    private ProductService productService;
    private ProductRepository productRepository;
    private ProductDtoConverter productDtoConverter;

    @Before
    public void setup() {
        productRepository = Mockito.mock(ProductRepository.class);
        productDtoConverter = Mockito.mock(ProductDtoConverter.class);

        productService = new ProductService(productRepository, productDtoConverter);
    }

    @Test
    public void whenCreateProductCalled_ItShouldReturnCreatedProductFromRepository() {
        ProductRequest productRequest = generateProductRequest();
        Product product = generateProduct();

        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productRepository.save(product)).thenReturn(product);

        Mockito.when(productDtoConverter.convert(product)).thenReturn(expectedProduct);

        ProductDto result = productService.createProduct(productRequest);

        Assert.assertEquals(result, expectedProduct);
    }

    @Test
    public void whenUpdateProductCalled_ItShouldReturnUpdatedProductFromRepository() {
        ProductRequest updateProductRequest = generateProductRequest();

        Product product = generateProduct();
        Mockito.when(productRepository.findById("product-id")).thenReturn(Optional.of(product));
        ProductDto expectedProduct = generateProductDto();
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(productDtoConverter.convert(product)).thenReturn(expectedProduct);

        ProductDto result = productService.updateProduct("product-id", updateProductRequest);

        Assert.assertEquals(result, expectedProduct);
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenUpdateProductCalledAndProductIdDoesNotExist_ItShouldThrowException() {
        ProductRequest updateProductRequest = generateProductRequest();
        Mockito.when(productRepository.findById("product-id")).thenReturn(Optional.empty());
        productService.updateProduct("product-id", updateProductRequest);
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenDeleteProductCalledAndProductIdDoesNotExist_ItShouldThrowException() {
        Mockito.when(productRepository.findById("product-id")).thenReturn(Optional.empty());
        productService.deleteProduct("product-id");
    }

    @Test(expected = ProductNotFoundException.class)
    public void whenGetProductIdCalledAndProductIdDoesNotExist_ItShouldThrowException() {
        Mockito.when(productRepository.findById("product-id")).thenReturn(Optional.empty());
        productService.getProductById("product-id");
    }

    @Test
    public void whenGetProductIdCalledWithExistProductId_ItShouldReturnProductDto() {
        Product product = generateProduct();
        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productRepository.findById("product-id")).thenReturn(Optional.of(product));
        Mockito.when(productDtoConverter.convert(product)).thenReturn(expectedProduct);

        ProductDto result = productService.getProductById("product-id");

        Assert.assertEquals(result, expectedProduct);
    }

    @Test
    public void whenGetAllProductsCalled_ItShouldReturnAllProducts() {
        List<ProductDto> expectedProductList = Arrays.asList(generateProductDto(), generateProductDto());
        List<Product> productList = Arrays.asList(generateProduct(), generateProduct());

        Mockito.when(productRepository.findAll()).thenReturn(productList);
        Mockito.when(productDtoConverter.convert(productList.get(0))).thenReturn(expectedProductList.get(0));

        List<ProductDto> result = productService.getAllProducts();

        Assert.assertEquals(result, expectedProductList);


    }


}