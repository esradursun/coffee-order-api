package com.starbux.order.dto;

import com.starbux.order.model.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Product type is mandatory")
    private ProductType productType;

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @NotNull(message = "Product price is mandatory")
    @Min(value = 0, message = "Price should be greater than 0 amount")
    private Double price;

}
