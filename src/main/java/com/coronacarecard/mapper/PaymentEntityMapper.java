package com.coronacarecard.mapper;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.service.BusinessService;

public interface PaymentEntityMapper {
    Object toSessionCreateParams(OrderDetail orderDetail,BusinessService businessService) throws BusinessNotFoundException,PaymentAccountNotSetupException;
    CheckoutResponse toCheckoutResponse(Object session, OrderDetail orderDetail, BusinessService service) throws BusinessNotFoundException, PaymentAccountNotSetupException;
}
