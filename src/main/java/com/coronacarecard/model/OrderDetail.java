package com.coronacarecard.model;

import java.util.List;

public class OrderDetail {
    private String customerEmail;
    private String customerMobile;
    private List<OrderLine> orderLine;
    private Double total;
    // TODO this is not optimal eventually different business will have different payment system how will we handel that
    private PaymentSystem paymentSystem;

    class OrderLine  {
        private Long businessId;
        private Double quantity;
        private Double unitPrice;
        private Double tip;
    }
}
