package com.starbux.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportCustomerTotalAmountDto {
    private String customerName;
    private Double totalPrice;
}
