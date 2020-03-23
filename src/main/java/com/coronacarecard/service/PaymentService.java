package com.coronacarecard.service;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;

public interface PaymentService {
    /**
     * Prase the url based on the system to have transaction id
     * and also need the Order for which this transaction was made
     * We mark the Ordered as paid
     * @param paymentSystem
     * @param urlParams
     * @return
     */
    CheckoutResponse successPayment(PaymentSystem paymentSystem, String urlParams);

    /** Pase the url basend on the system and log why it failed and probably in futre alarm on it
     * @param paymentSystem
     * @param urlParams
     * @return
     */
    CheckoutResponse failedPayment(PaymentSystem paymentSystem, String urlParams);

    /**
     * When the user reaches the confirmation page we might want to use this method to confirm the transaction happened
     * We email the user the gift card code for each of the gifts purchased
     * We also mark the transaction as Completed, because
     * @param transactionId
     */
    void confirmTransaction(PaymentSystem paymentSystem, String transactionId);

    /**
     * Generate onboarding URL for the given payment system and business
     * @param paymentSystem
     * @param businessDAO
     * @return
     */
    String generateOnBoardingUrl(PaymentSystem paymentSystem, Business businessDAO);
}
