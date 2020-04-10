package com.coronacarecard.controller;

import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    ShoppingCartController shoppingCartController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void validate_customeremail_is_not_nullOrEmpty() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .customerEmail(null)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));

        orderDetail = OrderDetail.builder()
                .customerEmail("")
                .build();

        content = objectMapper.writeValueAsString(orderDetail);

        result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }

    @Test
    public void validate_customeremail_is_wellformed() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .customerEmail("bad_email")
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("must be a well-formed email address"));
    }

    @Test
    public void validate_orderline_is_not_empty() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .orderLine(null)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("must not be null"));

    }

    @Test
    public void validate_total_is_not_empty() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .total(null)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("must not be empty"));
    }

    @Test
    public void validate_total_is_not_less_than_zero() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .total(-1.0)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("must be greater than or equal to 0"));
    }

    @Test
    public void validate_contribution_is_not_less_than_zero() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .contribution(-1.0)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("must be greater than or equal to 0"));
    }

    @Test
    public void validate_processingfee_is_not_less_than_zero() throws Exception {
        OrderDetail orderDetail = OrderDetail.builder()
                .processingFee(-1.0)
                .build();

        String content = objectMapper.writeValueAsString(orderDetail);

        MvcResult result = mockMvc.perform(post("/cart/checkout")
                .contentType("application/json").content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("must be greater than or equal to 0"));
    }
}