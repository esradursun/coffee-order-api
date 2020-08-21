package com.starbux.order.service;

import com.starbux.order.model.*;
import com.starbux.order.service.strategy.DiscountCart;
import com.starbux.order.service.strategy.FreeProductDiscountStrategy;
import com.starbux.order.service.strategy.MultiplePromotionDiscountStrategy;
import com.starbux.order.service.strategy.PercentageDiscountStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderCalculationService {
    private static final Logger logger = LoggerFactory.getLogger(OrderCalculationService.class);

    private static final Double MINPRICEFORDISCOUNT = 12.0;

    public OrderFee calculateOrderFee(final List<OrderProducts> productList) {
        final Double originalPrice = calculateOriginalPrice(productList);
        logger.info("Original price is calculated, originalPrice: {}", originalPrice);

        return calculateDiscount(productList, originalPrice);
    }

    private Double calculateOriginalPrice(List<OrderProducts> productList) {
        return productList.stream()
                .map(this::calculatePriceForDrinkAndToppings)
                .reduce(BigDecimal.ZERO.doubleValue(), Double::sum);
    }

    private Double calculatePriceForDrinkAndToppings(OrderProducts orderProducts) {
        return orderProducts.getOrderDrinkToppingList()
                .stream()
                .findFirst()
                .map(OrderDrinkTopping::getDrink)
                .map(Product::getPrice)
                .orElse(BigDecimal.ZERO.doubleValue()) +
                orderProducts.getOrderDrinkToppingList()
                        .stream()
                        .filter(orderDrinkTopping -> orderDrinkTopping.getTopping() != null)
                        .map(OrderDrinkTopping::getTopping)
                        .map(Product::getPrice)
                        .reduce(BigDecimal.ZERO.doubleValue(), Double::sum);
    }

    private OrderFee calculateDiscount(List<OrderProducts> productList, Double originalPrice) {
        return getDiscount(productList, originalPrice)
                .map(discountFee -> OrderFee.builder()
                        .discountType(discountFee.getDiscountType())
                        .originalPrice(originalPrice)
                        .discountTotal(discountFee.getDiscountedPrice())
                        .totalPrice(originalPrice - discountFee.getDiscountedPrice())
                        .build())
                .orElse(OrderFee.builder()
                        .discountTotal(BigDecimal.ZERO.doubleValue())
                        .discountType(DiscountType.NODISCOUNTELIGIBLE)
                        .originalPrice(originalPrice)
                        .totalPrice(originalPrice)
                        .build());
    }

    private Optional<DiscountFee> getDiscount(List<OrderProducts> productList, Double originalPrice) {
        Optional<DiscountFee> discountFeeOptional = Optional.empty();

        if (isPercentDiscountAvailable(originalPrice) && isFreeCoffeeAvailable(productList)) {
            discountFeeOptional = Optional.of(new DiscountCart(new MultiplePromotionDiscountStrategy()).applyDiscountStrategy(productList, originalPrice));
            logger.info("Order is eligible for the both promotion. Order fee calculated by the lowest amount discount");
        } else if (isPercentDiscountAvailable(originalPrice)) {
            discountFeeOptional = Optional.of(new DiscountCart(new PercentageDiscountStrategy()).applyDiscountStrategy(productList, originalPrice));
            logger.info("Order Fee calculated by percentage discount");
        } else if (isFreeCoffeeAvailable(productList)) {
            discountFeeOptional = Optional.of(new DiscountCart(new FreeProductDiscountStrategy()).applyDiscountStrategy(productList, originalPrice));
            logger.info("Order Fee calculated by the lowest amount discount");
        } else {
            logger.info("There is no eligible discount");
        }
        return discountFeeOptional;
    }

    private Boolean isPercentDiscountAvailable(Double originalPrice) {
        return originalPrice > MINPRICEFORDISCOUNT;
    }

    private Boolean isFreeCoffeeAvailable(List<OrderProducts> productList) {
        return productList.stream().map(OrderProducts::getOrderDrinkToppingList).count() > 2;
    }

}
