package com.coronacarecard.model;

public enum PaymentState {

    /**
     * When the Payment is first created
     */
    DRAFT,

    /**
     * We have sent the payment to the processing system
     */
    DISPATCHED,

    /**
     * The payment is processed by the payment system and we received confirmation from it
     */
    PROCESSED,

    /**
     * The user has been notified about the purchase and its payment.
     */
    CONFIRMED

}
