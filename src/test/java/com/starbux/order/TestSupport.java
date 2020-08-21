package com.starbux.order;

import com.starbux.order.dto.*;
import com.starbux.order.model.*;

import java.util.*;

public class TestSupport {

    public static final String PRODUCTCONTROLLERPATH = "/v1/product";
    public static final String ORDERCONTROLLERPATH = "/v1/order";
    public static final String REPORTCONTROLLERPATH = "/v1/report";


    public Orders generateOrder(){
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();

        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                    OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        OrderProducts orderProducts = new OrderProducts();
        orderDrinkToppingList.forEach(orderProducts::addToOrderDrinkTopping);

        OrderFee orderFee = OrderFee.builder().originalPrice(9.0).totalPrice(9.0).discountTotal(0.0).build();

        Orders orders = Orders.builder().id("order-id").orderFee(orderFee).orderStatus(OrderStatus.CREATED).customerName("alice").build();
        Arrays.asList(orderProducts).forEach(orders::addToOrderProducts);

        return orders;
    }

    public Orders generateUpdatedOrder(Orders orders, OrderStatus orderStatus){
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();

        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        OrderProducts orderProducts = new OrderProducts();
        orderProducts.setOrderDrinkToppingList(new ArrayList<>());
        orderDrinkToppingList.forEach(orderProducts::addToOrderDrinkTopping);

        OrderFee updateOrderFee = OrderFee.builder().originalPrice(18.0).totalPrice(13.5).discountTotal(4.5).discountType(DiscountType.PERCENTAGE).build();

        Orders updatedOrders = Orders.builder().id("order-id").orderFee(updateOrderFee).orderStatus(orderStatus).customerName("alice").productList(new ArrayList<>()).build();
        Arrays.asList(orderProducts).forEach(updatedOrders::addToOrderProducts);
        orders.getProductList().forEach(updatedOrders::addToOrderProducts);

        return updatedOrders;
    }


    public OrderRequest generateOrderRequest(){
        List<OrderDrinkRequest> orderDrinkRequestList = Arrays.asList(new OrderDrinkRequest("Black Coffee",Arrays.asList("Milk", "Hazelnut Syrup")));
        return new OrderRequest(orderDrinkRequestList, "alice");
    }

    public OrderDto generateOrderDto(OrderStatus orderStatus){
        return OrderDto.builder()
                .id("order-id")
                .customerName("alice")
                .orderStatus(orderStatus)
                .totalPrice(9.0)
                .discountTotal(0.0)
                .productList(Arrays.asList(OrderProductsDto.builder()
                        .drink(ProductDto.builder().name("Black Coffee").price(4.0).build())
                        .toppings(Arrays.asList(ProductDto.builder().name("Milk").price(2.0).build(),
                                ProductDto.builder().name("Hazelnut Syrup").price(3.0).build()))
                        .build()))
                .build();
    }

    public OrderDto generateUpdatedOrderDto(OrderStatus orderStatus){
        return OrderDto.builder()
                .id("order-id")
                .customerName("alice")
                .orderStatus(orderStatus)
                .originalPrice(18.0)
                .discountTotal(4.5)
                .totalPrice(13.5)
                .productList(Arrays.asList(OrderProductsDto.builder()
                                .drink(ProductDto.builder().name("Black Coffee").price(4.0).build())
                                .toppings(Arrays.asList(ProductDto.builder().name("Milk").price(2.0).build(),
                                        ProductDto.builder().name("Hazelnut Syrup").price(3.0).build()))
                                .build(),
                        OrderProductsDto.builder()
                                .drink(ProductDto.builder().name("Black Coffee").price(4.0).build())
                                .toppings(Arrays.asList(ProductDto.builder().name("Milk").price(2.0).build(),
                                        ProductDto.builder().name("Hazelnut Syrup").price(3.0).build()))
                                .build()))
                .build();
    }

    public Product generateProduct() {
        return Product.builder()
                .name("Black Coffee").price(6.0).productType(ProductType.DRINK).build();
    }

    public ProductDto generateProductDto() {
        return ProductDto.builder()
                .productId("product-id")
                .productType(ProductType.DRINK)
                .name("Black Coffee")
                .price(6.0)
                .build();
    }

    public ProductRequest generateProductRequest(){
        return new ProductRequest(ProductType.DRINK,"Black Coffee", 6.0);
    }
}
