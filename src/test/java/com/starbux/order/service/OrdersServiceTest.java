package com.starbux.order.service;

import com.starbux.order.TestSupport;
import com.starbux.order.dto.*;
import com.starbux.order.exception.InvalidOrderRequestException;
import com.starbux.order.exception.InvalidOrderStatusException;
import com.starbux.order.exception.OrderNotFoundException;
import com.starbux.order.model.*;
import com.starbux.order.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

public class OrdersServiceTest extends TestSupport {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderDtoConverter orderDtoConverter;
    private OrderCalculationService orderCalculationService;
    private ReportMostUsedToppingsRepository reportMostUsedToppingsRepository;

    @Before
    public void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        orderDtoConverter = Mockito.mock(OrderDtoConverter.class);
        orderCalculationService = Mockito.mock(OrderCalculationService.class);
        reportMostUsedToppingsRepository = Mockito.mock(ReportMostUsedToppingsRepository.class);
        orderService = new OrderService(orderRepository, productRepository, orderDtoConverter, orderCalculationService, reportMostUsedToppingsRepository);
    }

    @Test
    public void whenCreateOrderCalledWithDrinkAndToppingList_ItShouldReturnCreatedOrderFromRepository() {
        OrderRequest orderRequest = generateOrderRequest();

        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();

        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        List<OrderProducts> orderProducts = Arrays.asList(OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build());
        OrderFee orderFee = OrderFee.builder().originalPrice(9.0).totalPrice(9.0).discountTotal(0.0).build();

        Orders orders = Orders.builder().orderFee(orderFee).orderStatus(OrderStatus.CREATED).customerName("alice").productList(orderProducts).build();

        OrderDto expectedOrder = generateOrderDto(OrderStatus.CREATED);

        Mockito.when(productRepository.findByName("Black Coffee")).thenReturn(blackCoffee);
        Mockito.when(productRepository.findByName("Milk")).thenReturn(milk);
        Mockito.when(productRepository.findByName("Hazelnut Syrup")).thenReturn(hazelnut);

        Mockito.when(orderCalculationService.calculateOrderFee(orderProducts)).thenReturn(orderFee);

        Mockito.when(orderRepository.save(orders)).thenReturn(orders);

        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(expectedOrder);

        OrderDto result = orderService.createOrder(orderRequest);

        Assert.assertEquals(result, expectedOrder);
    }

    @Test
    public void whenCreateOrderCalledWithDrinkAndWithoutTopping_ItShouldReturnCreatedOrderFromRepository() {
        OrderRequest orderRequest = new OrderRequest(Arrays.asList(new OrderDrinkRequest("Black Coffee", new ArrayList<>())),"alice");
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).build());

        List<OrderProducts> orderProducts = Arrays.asList(OrderProducts.builder().orderDrinkToppingList(orderDrinkToppingList).build());
        OrderFee orderFee = OrderFee.builder().originalPrice(4.0).totalPrice(4.0).discountTotal(0.0).build();

        Orders orders = Orders.builder().orderFee(orderFee).orderStatus(OrderStatus.CREATED).customerName("alice").productList(orderProducts).build();

        OrderDto expectedOrder = generateOrderDto(OrderStatus.CREATED);

        Mockito.when(productRepository.findByName("Black Coffee")).thenReturn(blackCoffee);

        Mockito.when(orderCalculationService.calculateOrderFee(orderProducts)).thenReturn(orderFee);

        Mockito.when(orderRepository.save(orders)).thenReturn(orders);

        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(expectedOrder);

        OrderDto result = orderService.createOrder(orderRequest);

        Assert.assertEquals(result, expectedOrder);
    }

    @Test(expected = InvalidOrderRequestException.class)
    public void whenCreateOrderCalledWithoutDrinkAndWithTopping_ItShouldThrowInvalidOrderRequestException() {
        OrderRequest orderRequest = new OrderRequest(Arrays.asList(new OrderDrinkRequest("", Arrays.asList("milk"))),"alice");
        orderService.createOrder(orderRequest);
    }

    @Test(expected = InvalidOrderRequestException.class)
    public void whenCreateOrderCalledWithInvalidDrinkAndInvalidTopping_ItShouldThrowInvalidOrderRequestException() {
        OrderRequest orderRequest = new OrderRequest(Arrays.asList(new OrderDrinkRequest("Filter Coffee", Arrays.asList("Caramel Syrup"))),"alice");
        orderService.createOrder(orderRequest);
    }

    @Test(expected = InvalidOrderRequestException.class)
    public void whenCreateOrderCalledWithValidDrinkAndInvalidTopping_ItShouldThrowInvalidOrderRequestException() {
        OrderRequest orderRequest = new OrderRequest(Arrays.asList(new OrderDrinkRequest("Black Coffee", Arrays.asList("Caramel Syrup"))),"alice");
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();

        Mockito.when(productRepository.findByName("Black Coffee")).thenReturn(blackCoffee);

        orderService.createOrder(orderRequest);
    }

    @Test
    public void whenUpdateOrderCalledAndOrderStatusIsCreated_ItShouldReturnUpdatedOrderFromRepository() {
        OrderRequest orderRequest = generateOrderRequest();
        Orders orders = generateOrder();

        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();

        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        OrderProducts orderProducts = new OrderProducts();
        orderDrinkToppingList.forEach(orderProducts::addToOrderDrinkTopping);

        OrderFee updateOrderFee = OrderFee.builder().originalPrice(18.0).totalPrice(13.5).discountTotal(4.5).discountType(DiscountType.PERCENTAGE).build();

        Orders updatedOrders = Orders.builder().id("order-id").orderFee(updateOrderFee).orderStatus(OrderStatus.UPDATED).customerName("alice").build();
        Arrays.asList(orderProducts).forEach(updatedOrders::addToOrderProducts);
        orders.getProductList().forEach(updatedOrders::addToOrderProducts);

        OrderDto expectedOrder = generateUpdatedOrderDto(OrderStatus.CREATED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        Mockito.when(productRepository.findByName("Black Coffee")).thenReturn(blackCoffee);
        Mockito.when(productRepository.findByName("Milk")).thenReturn(milk);
        Mockito.when(productRepository.findByName("Hazelnut Syrup")).thenReturn(hazelnut);

        Mockito.when(orderCalculationService.calculateOrderFee(updatedOrders.getProductList())).thenReturn(updateOrderFee);

        Mockito.when(orderRepository.save(updatedOrders)).thenReturn(updatedOrders);
        Mockito.when(orderDtoConverter.convert(updatedOrders)).thenReturn(expectedOrder);

        OrderDto result = orderService.updateOrder("order-id", orderRequest);

        Assert.assertEquals(result, expectedOrder);
    }

    @Test
    public void whenUpdateOrderCalledAndOrderStatusIsUpdate_ItShouldReturnUpdatedOrderFromRepository() {
        OrderRequest orderRequest = generateOrderRequest();
        Orders orders = generateOrder();

        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product hazelnut = Product.builder().name("Hazelnut Syrup").price(3.0).productType(ProductType.TOPPING).build();

        List<OrderDrinkTopping> orderDrinkToppingList =
                Arrays.asList(OrderDrinkTopping.builder().drink(blackCoffee).topping(milk).build(),
                        OrderDrinkTopping.builder().drink(blackCoffee).topping(hazelnut).build());

        OrderProducts orderProducts = new OrderProducts();
        orderDrinkToppingList.forEach(orderProducts::addToOrderDrinkTopping);

        OrderFee updateOrderFee = OrderFee.builder().originalPrice(18.0).totalPrice(13.5).discountTotal(4.5).discountType(DiscountType.PERCENTAGE).build();

        Orders updatedOrders = Orders.builder().id("order-id").orderFee(updateOrderFee).orderStatus(OrderStatus.UPDATED).customerName("alice").build();
        Arrays.asList(orderProducts).forEach(updatedOrders::addToOrderProducts);
        orders.getProductList().forEach(updatedOrders::addToOrderProducts);

        OrderDto expectedOrder = generateUpdatedOrderDto(OrderStatus.UPDATED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        Mockito.when(productRepository.findByName("Black Coffee")).thenReturn(blackCoffee);
        Mockito.when(productRepository.findByName("Milk")).thenReturn(milk);
        Mockito.when(productRepository.findByName("Hazelnut Syrup")).thenReturn(hazelnut);
        Mockito.when(orderCalculationService.calculateOrderFee(updatedOrders.getProductList())).thenReturn(updateOrderFee);
        Mockito.when(orderRepository.save(updatedOrders)).thenReturn(updatedOrders);
        Mockito.when(orderDtoConverter.convert(updatedOrders)).thenReturn(expectedOrder);

        OrderDto result = orderService.updateOrder("order-id", orderRequest);

        Assert.assertEquals(result, expectedOrder);
    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenUpdateOrderCalledAndOrderStatusIsFinish_itShouldThrowInvalidOrderStatusException() {
        OrderRequest orderRequest = generateOrderRequest();
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.FINISHED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        orderService.updateOrder("order-id", orderRequest);
    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenUpdateOrderCalledAndOrderStatusIsCancel_itShouldThrowInvalidOrderStatusException() {
        OrderRequest orderRequest = generateOrderRequest();
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.CANCELLED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        orderService.updateOrder("order-id", orderRequest);
    }

    @Test(expected = OrderNotFoundException.class)
    public void whenUpdateOrderCalledForNonExistOrder_ItShouldThrowException() {
        OrderRequest orderRequest = generateOrderRequest();
        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.empty());
        orderService.updateOrder("order-id", orderRequest);
    }

    @Test
    public void whenFinishOrderCalledForOrderStatusIsCreated_itShouldUpdateOrderStatusWithFinished() {
        Orders orders = generateOrder();

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));

        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(generateOrderDto(OrderStatus.FINISHED));

        OrderDto result = orderService.finishOrder("order-id");

        Assert.assertEquals(result.getOrderStatus(), OrderStatus.FINISHED);

    }

    @Test
    public void whenFinishOrderCalledForOrderStatusIsUpdated_itShouldUpdateOrderStatusWithFinished() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.UPDATED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));

        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(generateOrderDto(OrderStatus.FINISHED));

        OrderDto result = orderService.finishOrder("order-id");


        Assert.assertEquals(result.getOrderStatus(), OrderStatus.FINISHED);

    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenFinishOrderCalledForOrderStatusIsCancelled_itShouldThrowInvalidOrderStatusException() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.CANCELLED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        orderService.finishOrder("order-id");
    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenFinishOrderCalledForOrderStatusIsFinished_itShouldThrowInvalidOrderStatusException() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.FINISHED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));

        orderService.finishOrder("order-id");
    }

    @Test(expected = OrderNotFoundException.class)
    public void whenFinishOrderCalledForNonExistOrder_itShouldThrowException() {
        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.empty());

        orderService.finishOrder("order-id");
    }

    @Test
    public void whenCancelOrderCalledForOrderStatusIsCreated_itShouldUpdateOrderStatusWithCancelled() {
        Orders orders = generateOrder();

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(generateOrderDto(OrderStatus.CANCELLED));

        OrderDto result = orderService.cancelOrder("order-id");

        Assert.assertEquals(result.getOrderStatus(), OrderStatus.CANCELLED);

    }

    @Test
    public void whenCancelOrderCalledForOrderStatusIsUpdated_itShouldUpdateOrderStatusWithCancelled() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.UPDATED);
        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        Mockito.when(orderDtoConverter.convert(orders)).thenReturn(generateOrderDto(OrderStatus.CANCELLED));

        OrderDto result = orderService.cancelOrder("order-id");

        Assert.assertEquals(result.getOrderStatus(), OrderStatus.CANCELLED);

    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenCancelOrderCalledForOrderStatusIsCancelled_itShouldThrowInvalidOrderStatusException() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.CANCELLED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));
        orderService.cancelOrder("order-id");
    }

    @Test(expected = InvalidOrderStatusException.class)
    public void whenCancelOrderCalledForOrderStatusIsFinished_itShouldThrowInvalidOrderStatusException() {
        Orders orders = generateOrder();
        orders.setOrderStatus(OrderStatus.FINISHED);

        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.of(orders));

        orderService.cancelOrder("order-id");
    }

    @Test(expected = OrderNotFoundException.class)
    public void whenCancelOrderCalledForNonExistOrder_itShouldThrowException() {
        Mockito.when(orderRepository.findById("order-id")).thenReturn(Optional.empty());

        orderService.finishOrder("order-id");
    }


}