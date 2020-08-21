package com.starbux.order.controller;

import com.starbux.order.dto.OrderDto;
import com.starbux.order.dto.OrderRequest;
import com.starbux.order.model.Orders;
import com.starbux.order.service.OrderService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/order")
@Api(description = "This Order API provides to create, update, cancel, finish order and gets an order by order id and all orders.")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ApiOperation(value = "This endpoint is for creating Order", produces = "Application/JSON", response = OrderDto.class, httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully order created"),
            @ApiResponse(code = 400, message = "Invalid Order Request")})
    public ResponseEntity<OrderDto> createOrder(
            @ApiParam(value = "Order creates by OrderRequest json object. It should consist at least one drink.", required = true, type = "OrderRequest")
            @Valid @RequestBody OrderRequest orderRequest) {

        logger.info("Order is creating");
        OrderDto orderDto = orderService.createOrder(orderRequest);
        Link link = linkTo(methodOn(OrderController.class).getOrder(orderDto.getId())).withSelfRel();
        Link updateOrderLink = linkTo(methodOn(OrderController.class).updateOrder(orderDto.getId(), orderRequest)).withRel("updateOrder");
        orderDto.add(link, updateOrderLink);
        logger.info("Order created by following order id: {}", orderDto.getId());
        return ResponseEntity.ok(orderDto);
    }

    @PutMapping("update-order/{orderId}")
    @ApiOperation(value = "This endpoint is for updating Order with order id", produces = "Application/JSON", response = OrderDto.class, httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully order updated"),
            @ApiResponse(code = 404, message = "Order not found"),
            @ApiResponse(code = 400, message = "Invalid Order Request")})
    public ResponseEntity<OrderDto> updateOrder(
            @ApiParam(value = "Order updates by valid order id.", required = true, type = "String")
            @PathVariable("orderId") String orderId,
            @ApiParam(value = "Order product list updates by OrderRequest json object. It should consist at least one drink.", required = true, type = "OrderRequest")
            @Valid @RequestBody OrderRequest orderRequest) {
        logger.info("Order is updating");
        OrderDto orderDto = orderService.updateOrder(orderId, orderRequest);
        Link link = linkTo(methodOn(OrderController.class).getOrder(orderDto.getId())).withSelfRel();
        orderDto.add(link);
        logger.info("Order updated by following order id: {}", orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PutMapping("finish-order/{orderId}")
    @ApiOperation(value = "This endpoint is for finalizing the order by order id", produces = "Application/JSON", response = OrderDto.class, httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully order finished"),
            @ApiResponse(code = 404, message = "Order not found")})
    public ResponseEntity<OrderDto> finishOrder(
            @ApiParam(value = "Order can be finished by valid order id", required = true, type = "String")
            @PathVariable("orderId") String orderId) {
        logger.info("Order is finishing");
        OrderDto orderDto = orderService.finishOrder(orderId);
        Link link = linkTo(methodOn(OrderController.class).getOrder(orderDto.getId())).withSelfRel();
        orderDto.add(link);
        logger.info("Order finished by following order id: {}", orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PutMapping("cancel-order/{orderId}")
    @ApiOperation(value = "This endpoint is for canceling order by order id", produces = "Application/JSON", response = OrderDto.class, httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully order cancelled"),
            @ApiResponse(code = 404, message = "Order not found")})
    public ResponseEntity<OrderDto> cancelOrder(
            @ApiParam(value = "Order can be cancelled by valid order id", required = true, type = "String")
            @PathVariable("orderId") String orderId) {
        logger.info("Order is cancelling");
        OrderDto orderDto = orderService.cancelOrder(orderId);
        Link link = linkTo(methodOn(OrderController.class).getOrder(orderDto.getId())).withSelfRel();
        orderDto.add(link);
        logger.info("Order cancelled by following order id: {}", orderId);
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/{orderId}")
    @ApiOperation(value = "This endpoint is for getting order by order id", produces = "Application/JSON", response = OrderDto.class, httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Order fetched successfully"),
            @ApiResponse(code = 404, message = "Order not found")})
    public ResponseEntity<OrderDto> getOrder(
            @ApiParam(value = "An order fetches by valid order id", required = true, type = "String")
            @PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping
    @ApiOperation(value = "This endpoint is for getting all orders", produces = "Application/JSON", response = OrderDto.class, httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders fetched successfully")})
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


}
