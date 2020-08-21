package com.starbux.order.service;

import com.google.common.collect.ImmutableMap;
import com.starbux.order.TestSupport;
import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ReportCustomerTotalAmountDto;
import com.starbux.order.dto.ReportMostUsedToppingsDto;
import com.starbux.order.dto.ReportMostUsedToppingsDtoConverter;
import com.starbux.order.model.Product;
import com.starbux.order.model.ProductType;
import com.starbux.order.model.ReportMostUsedToppings;
import com.starbux.order.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportServiceTest extends TestSupport {

    private OrderRepositoryImpl orderRepository;
    private ReportMostUsedToppingsRepository reportMostUsedToppingsRepository;
    private ReportMostUsedToppingsDtoConverter reportMostUsedToppingsDtoConverter;
    private ReportService reportService;

    @Before
    public void setup(){
        orderRepository = Mockito.mock(OrderRepositoryImpl.class);
        reportMostUsedToppingsRepository = Mockito.mock(ReportMostUsedToppingsRepository.class);
        reportMostUsedToppingsDtoConverter = Mockito.mock(ReportMostUsedToppingsDtoConverter.class);

        reportService = new ReportService(orderRepository, reportMostUsedToppingsRepository, reportMostUsedToppingsDtoConverter);
    }

    @Test
    public void whenGetMostUsedToppingsOfDrinksReport_itShouldReturnReportMostUsedToppingsDtoList(){
        Product blackCoffee = Product.builder().name("Black Coffee").price(4.0).productType(ProductType.DRINK).build();
        Product milk = Product.builder().name("Milk").price(2.0).productType(ProductType.TOPPING).build();
        Product tea = Product.builder().name("Tea").price(4.0).productType(ProductType.DRINK).build();
        Product hazelnutSyrup = Product.builder().name("Hazelnut Syrup").price(2.0).productType(ProductType.TOPPING).build();

        List<ReportMostUsedToppings> reportMostUsedToppings = Arrays.asList(
                ReportMostUsedToppings.builder().drink(blackCoffee).topping(milk).toppingCount(5).build(),
                ReportMostUsedToppings.builder().drink(tea).topping(milk).toppingCount(4).build(),
                ReportMostUsedToppings.builder().drink(blackCoffee).topping(hazelnutSyrup).toppingCount(1).build(),
                ReportMostUsedToppings.builder().drink(tea).topping(hazelnutSyrup).toppingCount(3).build()
        );

        List<ReportMostUsedToppings> expectedReportModel = Arrays.asList(
                ReportMostUsedToppings.builder().drink(blackCoffee).topping(milk).toppingCount(5).build(),
                ReportMostUsedToppings.builder().drink(tea).topping(milk).toppingCount(4).build()
        );

        ProductDto blackCoffeeDto = generateProductDto();
        ProductDto milkDto = ProductDto.builder()
                .productId("product-id")
                .productType(ProductType.TOPPING)
                .name("Milk")
                .price(2.0)
                .build();
        ProductDto teaDto = ProductDto.builder()
                .productId("product-id")
                .productType(ProductType.DRINK)
                .name("Tea")
                .price(4.0)
                .build();
        List<ReportMostUsedToppingsDto> expectedReportList = Arrays.asList(
                ReportMostUsedToppingsDto.builder().drink(blackCoffeeDto).topping(milkDto).toppingCount(5).build(),
                ReportMostUsedToppingsDto.builder().drink(teaDto).topping(milkDto).toppingCount(4).build());


        Mockito.when(reportMostUsedToppingsRepository.findAllByOrderByToppingCountDesc()).thenReturn(reportMostUsedToppings);
        Mockito.when(reportMostUsedToppingsDtoConverter.convert(expectedReportModel.get(0))).thenReturn(expectedReportList.get(0));
        Mockito.when(reportMostUsedToppingsDtoConverter.convert(expectedReportModel.get(1))).thenReturn(expectedReportList.get(1));

        Assert.assertEquals(expectedReportList, reportService.getMostUsedToppingsOfDrinksReport());

    }

    @Test
    public void whenGetTotalAmountOfOrdersPerCustomerReport_itShouldReturnReportCustomerTotalAmountDtoList(){

        Map<String, Double> totalAmountOfCustomers = ImmutableMap.of("alice", 15.0, "bob" ,12.0);
        List<ReportCustomerTotalAmountDto> expectedReportList = Arrays.asList(
                ReportCustomerTotalAmountDto.builder().customerName("alice").totalPrice(15.0).build(),
                ReportCustomerTotalAmountDto.builder().customerName("bob").totalPrice(12.0).build());

        Mockito.when(orderRepository.getTotalAmountOfOrdersPerCustomer()).thenReturn(totalAmountOfCustomers);

        Assert.assertEquals(expectedReportList, reportService.getTotalAmountOfOrdersPerCustomerReport());
    }




}