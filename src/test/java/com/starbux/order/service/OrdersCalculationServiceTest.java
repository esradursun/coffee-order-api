package com.starbux.order.service;

import com.starbux.order.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class OrdersCalculationServiceTest {

    private OrderCalculationService orderCalculationService;

    @Before
    public void setUp() throws Exception {
        orderCalculationService = new OrderCalculationService();
    }

    @Test
    public void whenCalculateOrderCalledForMoreThan12EurosOrder_itShouldApplyPercentageDiscount() {
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();
        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        List<OrderProducts> orderProducts = Arrays.asList(OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build(),
                OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build());


        OrderFee expectedOrderFee = OrderFee.builder().originalPrice(18.0).discountTotal(4.5).totalPrice(13.5).discountType(DiscountType.PERCENTAGE).build();

        OrderFee result = orderCalculationService.calculateOrderFee(orderProducts);

        Assert.assertEquals(result, expectedOrderFee);
    }

    @Test
    public void whenCalculateOrderCalledForMoreThan3Product_itShouldApplyFreeCoffeeDiscount() {
        Product tea = Product.builder().name("Tea").price(3.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(tea).topping(milk).build());

        List<OrderProducts> orderProducts = Arrays.asList(OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build(),
                OrderProducts.builder().orderDrinkToppingList(Arrays.asList(OrderDrinkTopping.builder().drink(tea).build())).build(),
                OrderProducts.builder().orderDrinkToppingList(Arrays.asList(OrderDrinkTopping.builder().drink(tea).build())).build());

        OrderFee expectedOrderFee = OrderFee.builder().originalPrice(11.0).discountTotal(3.0).totalPrice(8.0).discountType(DiscountType.FREECOFFEE).build();

        OrderFee result = orderCalculationService.calculateOrderFee(orderProducts);

        Assert.assertEquals(result, expectedOrderFee);
    }

    @Test
    public void whenCalculateOrderCalledAndTheCartIsEligibleForBothPromotion_itShouldApplyLowestAmount() {
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product tea = Product.builder().name("Tea").price(3.0).productType(ProductType.DRINK).build();
        Product lemon = Product.builder().name("Lemon").price(2.0).productType(ProductType.TOPPING).build();
        Product mocha = Product.builder().name("Mocha").price(6.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();

        List<OrderProducts> orderProducts = Arrays.asList(
                OrderProducts.builder().orderDrinkToppingList(Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(lemon).build())).build(),
                OrderProducts.builder().orderDrinkToppingList(Arrays.asList(OrderDrinkTopping.builder().drink(tea).topping(lemon).build())).build(),
                OrderProducts.builder().orderDrinkToppingList(Arrays.asList(OrderDrinkTopping.builder().drink(mocha).build())).build()
        );

        OrderFee expectedOrderFee = OrderFee.builder().originalPrice(19.0).discountTotal(4.75).totalPrice(14.25).discountType(DiscountType.PERCENTAGE).build();

        OrderFee result = orderCalculationService.calculateOrderFee(orderProducts);

        Assert.assertEquals(result, expectedOrderFee);
    }

    @Test
    public void whenCalculateOrderCalledWithoutDiscountPromotion_itShouldReturnTotalPriceWithoutDiscount() {
        Product tea = Product.builder().name("Tea").price(3.0).productType(ProductType.DRINK).build();
        Product lemon = Product.builder().name("Lemon").price(2.0).productType(ProductType.TOPPING).build();
        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(tea).topping(lemon).build());

        List<OrderProducts> orderProducts = Arrays.asList(OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build());

        OrderFee expectedOrderFee = OrderFee.builder().discountTotal(0.0).discountType(DiscountType.NODISCOUNTELIGIBLE).originalPrice(5.0).totalPrice(5.0).build();

        OrderFee result = orderCalculationService.calculateOrderFee(orderProducts);

        Assert.assertEquals(result, expectedOrderFee);
    }
}