package com.starbux.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportMostUsedToppingsDto {
    private ProductDto drink;
    private ProductDto topping;
    private Integer toppingCount;
}
