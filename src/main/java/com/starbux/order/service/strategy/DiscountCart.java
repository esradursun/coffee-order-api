package com.starbux.order.service.strategy;

import com.starbux.order.model.DiscountFee;
import com.starbux.order.model.OrderProducts;

import java.util.List;

public class DiscountCart {

    private DiscountStrategy discountStrategy;

    public DiscountCart(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public DiscountFee applyDiscountStrategy(List<OrderProducts> productList, Double originalPrice) {
        return discountStrategy.applyDiscount(productList, originalPrice);
    }
}
