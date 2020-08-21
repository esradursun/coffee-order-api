package com.starbux.order.dto;

import com.starbux.order.model.DiscountType;
import com.starbux.order.model.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
public class OrderDto extends RepresentationModel<OrderDto> {
    private String id;
    private List<OrderProductsDto> productList;
    private Double originalPrice;
    private Double discountTotal;
    private Double totalPrice;
    private DiscountType discountType;
    private OrderStatus orderStatus;
    private String customerName;


}
