package com.starbux.order.dto;

import com.starbux.order.model.Orders;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderDtoConverter {

    private final OrderProductsDtoConverter orderProductsDtoConverter;

    public OrderDtoConverter(OrderProductsDtoConverter orderProductsDtoConverter) {
        this.orderProductsDtoConverter = orderProductsDtoConverter;
    }

    public OrderDto convert(Orders from) {
        return OrderDto.builder()
                .productList(from.getProductList()
                        .stream()
                        .map(orderProductsDtoConverter::convert)
                        .collect(Collectors.toList()))
                .id(from.getId())
                .originalPrice(from.getOrderFee().getOriginalPrice())
                .discountTotal(from.getOrderFee().getDiscountTotal())
                .discountType(from.getOrderFee().getDiscountType())
                .totalPrice(from.getOrderFee().getTotalPrice())
                .orderStatus(from.getOrderStatus())
                .customerName(from.getCustomerName())
                .build();
    }
}
