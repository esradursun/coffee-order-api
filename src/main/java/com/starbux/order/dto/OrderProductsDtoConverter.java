package com.starbux.order.dto;

import com.starbux.order.model.OrderDrinkTopping;
import com.starbux.order.model.OrderProducts;
import com.starbux.order.model.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderProductsDtoConverter {

    private final ProductDtoConverter productDtoConverter;

    public OrderProductsDtoConverter(ProductDtoConverter productDtoConverter) {
        this.productDtoConverter = productDtoConverter;
    }

    public OrderProductsDto convert(OrderProducts from) {

        List<OrderDrinkTopping> orderDrinkToppingList = from.getOrderDrinkToppingList();
        List<Product> toppings = orderDrinkToppingList.stream().map(OrderDrinkTopping::getTopping).collect(Collectors.toList());
        OrderProductsDto to = new OrderProductsDto();

        to.setDrink(productDtoConverter.convert(orderDrinkToppingList.get(0).getDrink()));
        if (from.getOrderDrinkToppingList().get(0).getTopping() != null) {
            to.setToppings(toppings.stream()
                    .map(productDtoConverter::convert)
                    .collect(Collectors.toList()));
        } else {
            to.setToppings(Collections.emptyList());
        }

        return to;

    }

}
