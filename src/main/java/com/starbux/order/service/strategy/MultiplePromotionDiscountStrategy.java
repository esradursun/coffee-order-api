package com.starbux.order.service.strategy;

import com.starbux.order.model.DiscountFee;
import com.starbux.order.model.OrderProducts;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class MultiplePromotionDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountFee applyDiscount(List<OrderProducts> productList, Double originalPrice) {
        DiscountFee percentDiscountAmount = new PercentageDiscountStrategy().applyDiscount(productList, originalPrice);
        DiscountFee freeCoffeePrice = new FreeProductDiscountStrategy().applyDiscount(productList, originalPrice);
        return Stream.of(percentDiscountAmount, freeCoffeePrice).min(Comparator.comparing(DiscountFee::getDiscountedPrice)).orElseThrow(NoSuchElementException::new);
    }
}
