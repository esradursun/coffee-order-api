package com.starbux.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starbux.order.TestSupport;
import com.starbux.order.dto.ProductDto;
import com.starbux.order.dto.ProductRequest;
import com.starbux.order.service.ProductService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server-port=0")
@RunWith(SpringRunner.class)
@DirtiesContext
public class ProductControllerTest extends TestSupport {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(roles="ADMIN")
    public void whenCreateProductCalled_itShouldReturnCreatedProductJson() throws Exception {
        ProductRequest productRequest = generateProductRequest();
        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productService.createProduct(productRequest)).thenReturn(expectedProduct);

        this.mockMvc.perform(post(PRODUCTCONTROLLERPATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(productRequest)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedProduct), false))
                .andReturn();
    }


    @Test
    @WithMockUser(roles="ADMIN")
    public void whenUpdateProductCalled_itShouldReturnUpdatedProductJson() throws Exception {
        ProductRequest productRequest = generateProductRequest();
        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productService.updateProduct("product-id", productRequest)).thenReturn(expectedProduct);

        this.mockMvc.perform(put(PRODUCTCONTROLLERPATH + "/product-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(productRequest)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedProduct), false))
                .andReturn();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void whenDeleteProductCalled_itShouldReturn200() throws Exception {
        this.mockMvc.perform(delete(PRODUCTCONTROLLERPATH + "/product-id"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    @Test
    @WithMockUser(roles="ADMIN")
    public void whenGetAllProductCalled_itShouldReturnAllProductJson() throws Exception {
        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productService.getAllProducts()).thenReturn(Arrays.asList(expectedProduct));

        this.mockMvc.perform(get(PRODUCTCONTROLLERPATH))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(Arrays.asList(expectedProduct)), false))
                .andReturn();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void whenGetProductCalled_itShouldReturnAllProductJson() throws Exception {
        ProductDto expectedProduct = generateProductDto();

        Mockito.when(productService.getProductById("product-id")).thenReturn(expectedProduct);

        this.mockMvc.perform(get(PRODUCTCONTROLLERPATH + "/product-id"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedProduct), false))
                .andReturn();
    }
}