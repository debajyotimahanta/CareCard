package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.OrderDetail;

public interface PaymentService {
    /**
     * Prase the url based on the system to have transaction id
     * and also need the Order for which this transaction was made
     * We mark the Ordered as paid
     *
     * @param urlParams
     * @return
     */
    CheckoutResponse successPayment(String urlParams);

    /**
     * Pase the url basend on the system and log why it failed and probably in futre alarm on it
     *
     * @param urlParams
     * @return
     */
    CheckoutResponse failedPayment(String urlParams);

    /**
     * When the user reaches the confirmation page we might want to use this method to confirm the transaction happened
     * We email the user the gift card code for each of the gifts purchased
     * We also mark the transaction as Completed, because
     *
     * @param transactionId
     */
    void confirmTransaction(String transactionId);

    /**
     * Generate onboarding URL for the given payment system and business
     *
     * @param business
     * @return
     */
    String generateOnBoardingUrl(Business business);

    /**
     * This method is used to generate checkout session for the given Order
     *
     * @param savedOrder
     * @return
     */
    CheckoutResponse generateCheckoutSession(OrderDetail savedOrder) throws BusinessNotFoundException, PaymentAccountNotSetupException, InternalException;

    /**
     * Validate all details about the order is correct.
     * Its mostly used to check if the processing fee is correct and also the total adds up
     *
     * @param order
     */
    void validate(com.coronacarecard.model.orders.OrderDetail order);

    Business getBusinessDetails(String code);
}
