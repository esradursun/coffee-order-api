package com.starbux.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbux.order.TestSupport;
import com.starbux.order.dto.*;
import com.starbux.order.model.*;
import com.starbux.order.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server-port=0")
@RunWith(SpringRunner.class)
@DirtiesContext
public class OrdersControllerTest extends TestSupport {

    @MockBean
    private OrderService orderService;

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    public void whenOrderCreateCalled_itShouldReturnCreatedOrderJson() throws Exception {
        OrderRequest orderRequest = generateOrderRequest();

        OrderDto expectedOrder = generateOrderDto(OrderStatus.CREATED);

        Mockito.when(orderService.createOrder(orderRequest)).thenReturn(expectedOrder);

        this.mockMvc.perform(post(ORDERCONTROLLERPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedOrder), false))
                .andReturn();
    }

    @Test
    public void whenOrderUpdateCalled_itShouldReturnUpdatedOrderJson() throws Exception {
        OrderRequest orderRequest = generateOrderRequest();
        OrderDto expectedOrder = generateOrderDto(OrderStatus.UPDATED);

        Mockito.when(orderService.updateOrder("order-id", orderRequest)).thenReturn(expectedOrder);

        this.mockMvc.perform(put(ORDERCONTROLLERPATH + "/update-order/order-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedOrder), false))
                .andReturn();

    }

    @Test
    public void whenOrderFinishCalled_itShouldReturnFinishedOrderJson() throws Exception {
        OrderRequest orderRequest = generateOrderRequest();
        OrderDto expectedOrder = generateOrderDto(OrderStatus.FINISHED);

        Mockito.when(orderService.finishOrder("order-id")).thenReturn(expectedOrder);

        this.mockMvc.perform(put(ORDERCONTROLLERPATH + "/finish-order/order-id"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedOrder), false))
                .andReturn();

    }

    @Test
    public void whenOrderCancelCalled_itShouldReturnCancelledOrderJson() throws Exception {
        OrderDto expectedOrder = generateOrderDto(OrderStatus.CANCELLED);

        Mockito.when(orderService.cancelOrder("order-id")).thenReturn(expectedOrder);

        this.mockMvc.perform(put(ORDERCONTROLLERPATH + "/cancel-order/order-id"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedOrder), false))
                .andReturn();

    }

}