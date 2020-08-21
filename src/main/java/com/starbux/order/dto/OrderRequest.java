package com.starbux.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderRequest {

    @NotEmpty(message = "There should be at least one drink")
    private List<OrderDrinkRequest> orderDrinkRequestList;

    @NotBlank(message = "Customer name is mandatory")
    private String customerName;
}
