package com.coronacarecard.controller;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.OrderNotFoundException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.OrderConfirmationResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.ShoppingCartService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("cart")
public class ShoppingCartController {
    private static Log log = LogFactory.getLog(ShoppingCartController.class);

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody OrderDetail order) throws BusinessNotFoundException,
            PaymentAccountNotSetupException, InternalException {
        return shoppingCartService.checkout(PaymentSystem.STRIPE, order);
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
    public OrderConfirmationResponse orderDetails(@PathVariable UUID id) throws OrderNotFoundException {
        log.info(String.format("Getting order details after payment for %s", id));
        return OrderConfirmationResponse.builder()
                .orderDetail(shoppingCartService.getOrder(id))
                .build();

    }
}
