package com.starbux.order.service.strategy;

import com.starbux.order.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FreeProductDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountFee applyDiscount(List<OrderProducts> productList, Double originalPrice) {
        return DiscountFee.builder()
                .discountedPrice(Collections.min(productList.stream()
                        .map(prod -> prod.getOrderDrinkToppingList().get(0).getDrink().getPrice() +
                                prod.getOrderDrinkToppingList().stream()
                                        .filter(orderDrinkTopping -> orderDrinkTopping.getTopping() != null)
                                        .map(OrderDrinkTopping::getTopping).map(Product::getPrice).reduce(BigDecimal.ZERO.doubleValue(), Double::sum))
                        .collect(Collectors.toList())))
                .discountType(DiscountType.FREECOFFEE)
                .build();
    }
}
