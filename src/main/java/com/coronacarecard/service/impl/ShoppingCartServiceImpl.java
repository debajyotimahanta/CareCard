package com.coronacarecard.service.impl;

import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.OrderDetail;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Override
    public CheckoutResponse checkout(PaymentSystem paymentSystem, OrderDetail order) {
        return null;
    }
}
