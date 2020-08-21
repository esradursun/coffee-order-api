package com.starbux.order.dto;

import com.starbux.order.model.ProductType;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
public class ProductDto extends RepresentationModel<ProductDto> {
    private String productId;
    private ProductType productType;
    private String name;
    private Double price;
}
