package com.starbux.order.service.strategy;

import com.starbux.order.model.DiscountFee;
import com.starbux.order.model.DiscountType;
import com.starbux.order.model.OrderProducts;

import java.util.List;

public class PercentageDiscountStrategy implements DiscountStrategy {

    private static final Double PERCENTDISCOUNT = 0.25;

    @Override
    public DiscountFee applyDiscount(List<OrderProducts> productList, Double originalPrice) {
        return DiscountFee.builder()
                .discountedPrice(originalPrice * PERCENTDISCOUNT)
                .discountType(DiscountType.PERCENTAGE)
                .build();
    }
}
