package com.starbux.order.dto;

import com.starbux.order.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoConverter {

    public ProductDto convert(Product from) {

        return ProductDto.builder()
                .productId(from.getId())
                .name(from.getName())
                .productType(from.getProductType())
                .price(from.getPrice())
                .build();
    }
}
