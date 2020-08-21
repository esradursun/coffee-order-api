package com.starbux.order.service.strategy;

import com.starbux.order.model.DiscountFee;
import com.starbux.order.model.OrderProducts;

import java.util.List;

public interface DiscountStrategy {
    DiscountFee applyDiscount(List<OrderProducts> productList, Double originalPrice);
}
