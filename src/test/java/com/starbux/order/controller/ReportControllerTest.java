package com.starbux.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbux.order.TestSupport;
import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ReportCustomerTotalAmountDto;
import com.starbux.order.dto.ReportMostUsedToppingsDto;
import com.starbux.order.model.ProductType;
import com.starbux.order.service.ProductService;
import com.starbux.order.service.ReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server-port=0")
@RunWith(SpringRunner.class)
@DirtiesContext
public class ReportControllerTest extends TestSupport {


    @MockBean
    ReportService reportService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles="ADMIN")
    public void whenMostUsedToppingsOfDrinksReportCalled_itShouldReturnMostUsedToppingsForDrinks() throws Exception {
        ProductDto blackCoffee = generateProductDto();
        ProductDto milk = ProductDto.builder()
                .productId("product-id")
                .productType(ProductType.TOPPING)
                .name("Milk")
                .price(2.0)
                .build();
        List<ReportMostUsedToppingsDto> expectedReportList = Arrays.asList(ReportMostUsedToppingsDto.builder().drink(blackCoffee).topping(milk).toppingCount(1).build());

        Mockito.when(reportService.getMostUsedToppingsOfDrinksReport()).thenReturn(expectedReportList);

        this.mockMvc.perform(get(REPORTCONTROLLERPATH + "/most-used-toppings"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedReportList), false))
                .andReturn();

    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void whenTotalAmountOfOrdersPerCustomerReportCalled_itShouldReturnTotalAmountOfOrdersPerCustomerReport() throws Exception {

        List<ReportCustomerTotalAmountDto> expectedReportList = Arrays.asList(ReportCustomerTotalAmountDto.builder().customerName("alice").totalPrice(12.5).build());

        Mockito.when(reportService.getTotalAmountOfOrdersPerCustomerReport()).thenReturn(expectedReportList);

        this.mockMvc.perform(get(REPORTCONTROLLERPATH + "/get-total-amount-of-orders"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedReportList), false))
                .andReturn();

    }



}