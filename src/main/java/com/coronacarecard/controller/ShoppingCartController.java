package com.coronacarecard.controller;

import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.OrderDetail;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody OrderDetail order) {
        return shoppingCartService.checkout(PaymentSystem.STRIPE, order);
    }


}
