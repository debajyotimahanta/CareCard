package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.PaymentSystem;

public interface ShoppingCartService {
    /**
     * For the order details provided we do the following
     * 1. Validate total matches with sum of each line item + payment processing fee and tax and tip
     * 2. Call payement system to generate token and pass it back to client
     * 3. If it fails return failure code and the reason it failed
     * 4. Log the failure as error
     * @param order
     * @return
     */
    CheckoutResponse checkout(PaymentSystem paymentSystem, OrderDetail order) throws BusinessNotFoundException;
}
