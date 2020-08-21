package com.starbux.order.service;

import com.starbux.order.dto.OrderDto;
import com.starbux.order.dto.OrderDtoConverter;
import com.starbux.order.dto.OrderRequest;
import com.starbux.order.exception.InvalidOrderRequestException;
import com.starbux.order.exception.InvalidOrderStatusException;
import com.starbux.order.exception.OrderNotFoundException;
import com.starbux.order.model.*;
import com.starbux.order.repository.OrderRepository;
import com.starbux.order.repository.ProductRepository;
import com.starbux.order.repository.ReportMostUsedToppingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDtoConverter orderDtoConverter;
    private final OrderCalculationService orderCalculationService;
    private final ReportMostUsedToppingsRepository reportMostUsedToppingsRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        OrderDtoConverter orderDtoConverter,
                        OrderCalculationService orderCalculationService,
                        ReportMostUsedToppingsRepository reportMostUsedToppingsRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderDtoConverter = orderDtoConverter;
        this.orderCalculationService = orderCalculationService;
        this.reportMostUsedToppingsRepository = reportMostUsedToppingsRepository;
    }

    public OrderDto createOrder(final OrderRequest orderRequest) {
        List<OrderProducts> productList = getOrderProductList(orderRequest);

        OrderFee orderFee = orderCalculationService.calculateOrderFee(productList);

        logger.info("Order fee calculated including discount {}", orderFee);
        Orders orders = Orders.builder()
                .orderFee(orderFee)
                .orderStatus(OrderStatus.CREATED)
                .customerName(orderRequest.getCustomerName())
                .build();
        productList.forEach(orders::addToOrderProducts);
        orderRepository.save(orders);

        createReportMostUsedToppingRecord(productList);
        return orderDtoConverter.convert(orders);
    }

    public OrderDto updateOrder(final String orderId, final OrderRequest orderRequest) {
        Orders orders = findOrderById(orderId);
        if (checkOrderStatus(orders.getOrderStatus())) {
            List<OrderProducts> productList = getOrderProductList(orderRequest);
            productList.forEach(orders::addToOrderProducts);

            logger.info("{} more product added to order, orderId: {}", productList.size(), orderId);

            OrderFee orderFee = orders.getOrderFee();
            OrderFee updatedOrderFee = orderCalculationService.calculateOrderFee(orders.getProductList());
            orderFee.setDiscountTotal(updatedOrderFee.getDiscountTotal());
            orderFee.setDiscountType(updatedOrderFee.getDiscountType());
            orderFee.setOriginalPrice(updatedOrderFee.getOriginalPrice());
            orderFee.setTotalPrice(updatedOrderFee.getTotalPrice());

            orders.setOrderFee(orderFee);
            orders.setOrderStatus(OrderStatus.UPDATED);
            orders.setCustomerName(orderRequest.getCustomerName());
            orderRepository.save(orders);

            createReportMostUsedToppingRecord(productList);
        }

        return orderDtoConverter.convert(orders);
    }

    public OrderDto finishOrder(String orderId) {
        Orders orders = findOrderById(orderId);
        return updateOrderByStatus(orders, OrderStatus.FINISHED);
    }

    public OrderDto cancelOrder(String orderId) {
        return updateOrderByStatus(findOrderById(orderId), OrderStatus.CANCELLED);
    }

    public OrderDto getOrder(String orderId) {
        return orderDtoConverter.convert(findOrderById(orderId));
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    private void createReportMostUsedToppingRecord(List<OrderProducts> productList) {
        productList.stream().flatMap(product -> product.getOrderDrinkToppingList().stream())
                .forEach(orderDrinkTopping -> {
                    if (orderDrinkTopping.getTopping() != null) {
                        Optional<ReportMostUsedToppings> reportMostUsedToppings =
                                reportMostUsedToppingsRepository.findByDrinkIdAndToppingId(orderDrinkTopping.getDrink().getId(), orderDrinkTopping.getTopping().getId());
                        if (reportMostUsedToppings.isPresent()) {
                            reportMostUsedToppings.get().setToppingCount(reportMostUsedToppings.get().getToppingCount() + 1);
                            reportMostUsedToppingsRepository.save(reportMostUsedToppings.get());
                        } else {
                            reportMostUsedToppingsRepository.save(ReportMostUsedToppings.builder().drink(orderDrinkTopping.getDrink()).topping(orderDrinkTopping.getTopping()).toppingCount(1).build());
                        }
                    }
                });
    }

    private Orders findOrderById(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            logger.warn("Order could not found by orderId: {}", orderId);
            return new OrderNotFoundException("Order could not found by orderId: " + orderId);
        });
    }

    private OrderDto updateOrderByStatus(Orders orders, OrderStatus orderStatus) {
        if (checkOrderStatus(orders.getOrderStatus())) {
            orders.setOrderStatus(orderStatus);
            orderRepository.save(orders);
        }
        return orderDtoConverter.convert(orders);
    }

    private List<OrderProducts> getOrderProductList(OrderRequest orderRequest) {
        return orderRequest.getOrderDrinkRequestList()
                .stream()
                .map(orderDrinkRequest -> {
                    OrderProducts orderProducts = new OrderProducts();
                    List<OrderDrinkTopping> orderDrinkToppingList = new ArrayList<>();
                    Product drink = productRepository.findByName(orderDrinkRequest.getDrinkName());
                    if (drink == null) {
                        logger.warn("Requested order should consist of at least one drink");
                        throw new InvalidOrderRequestException("Requested order should consist of at least one drink");
                    }
                    if (orderDrinkRequest.getToppingNameList().size() == 0) {
                        orderDrinkToppingList.add(OrderDrinkTopping.builder().drink(drink).build());
                    } else {
                        List<Product> toppings = orderDrinkRequest.getToppingNameList().stream().map(productRepository::findByName).collect(Collectors.toList());
                        if (toppings.get(0) == null) {
                            logger.warn("Requested order should consist the valid topping");
                            throw new InvalidOrderRequestException("Requested order should consist the valid topping");
                        }
                        toppings.forEach(topping -> orderDrinkToppingList.add(OrderDrinkTopping.builder().drink(drink).topping(topping).build()));
                    }
                    orderDrinkToppingList.forEach(orderProducts::addToOrderDrinkTopping);

                    return orderProducts;
                })
                .collect(Collectors.toList());
    }

    private boolean checkOrderStatus(OrderStatus orderStatus) {
        if (orderStatus.equals(OrderStatus.CREATED) || orderStatus.equals(OrderStatus.UPDATED)) {
            return true;
        }
        logger.warn("Requested order status is {} It should be CREATED or UPDATED.", orderStatus);
        throw new InvalidOrderStatusException("Requested order status is " + orderStatus + " It should be CREATED or UPDATED.");
    }

}
