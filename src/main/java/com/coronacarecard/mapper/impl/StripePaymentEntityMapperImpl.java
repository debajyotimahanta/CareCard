package com.coronacarecard.mapper.impl;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.mapper.PaymentEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessAccountDetail;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.User;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.service.BusinessService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("StripeEntityMapper")
public class StripePaymentEntityMapperImpl implements PaymentEntityMapper {
    private final String PAYMENT_METHOD_TYPES="payment_method_types";
    private final String LINE_ITEMS="line_items";

    private final String LINE_ITEM_NAME="name";
    private final String LINE_ITEM_AMOUNT="amount";
    private final String LINE_ITEM_CURRENCY="currency";
    private final String LINE_ITEM_QUANTITY="quantity";
    private final String DESTINATION="destination";
    private final String TRANSFER_DATA="transfer_data";
    private final String PAYMENT_INTENT_DATA="payment_intent_data";
    private final String SUCCESS_URL="success_url";
    private final String CANCEL_URL="cancel_url";

    @Value("${spring.app.forntEndBaseUrl}")
    private String forntEndBaseUrl;

    @Value("${spring.app.appUrl}")
    private String appUrl;

    @Override
    public Object toSessionCreateParams(OrderDetail orderDetail, BusinessService businessService) throws BusinessNotFoundException, PaymentAccountNotSetupException {
        //TODO:Only one busiess can be part of a checkout, so getting the details of the first
        Business business = businessService.getBusiness(orderDetail.getOrderLine().get(0).getBusinessId());
        String accountId = business.getOwner()
                .flatMap(User::getBusinessAccountDetail)
                .map(BusinessAccountDetail::getExternalRefId)
                .orElse(null);
        Optional<User> maybeUser = business.getOwner();

        if (accountId == null || !maybeUser.isPresent()) {
            throw new PaymentAccountNotSetupException();
        }
        return SessionCreateParams.builder()
                .setCustomerEmail(maybeUser.get().getEmail())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addAllLineItem(orderDetail.getOrderLine()
                        .stream()
                        .map(ol -> createLineItems(orderDetail, ol))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
                )
                .setSuccessUrl(appUrl+"/payment/stripe/success")
                .setCancelUrl(appUrl+"/payment/stripe/cancel")
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData
                        .builder()
                        .setTransferData(SessionCreateParams.PaymentIntentData.TransferData
                                .builder()
                                .setDestination(accountId)
                                .build())
                        .build())
                .build();
    }

    @Override
    public CheckoutResponse toCheckoutResponse(Object session,OrderDetail orderDetail,BusinessService businessService) throws BusinessNotFoundException,PaymentAccountNotSetupException{
        Business business = businessService.getBusiness(orderDetail.getOrderLine().get(0).getBusinessId());
        String accountId = business.getOwner()
                .flatMap(User::getBusinessAccountDetail)
                .map(BusinessAccountDetail::getExternalRefId)
                .orElse(null);
        if(accountId==null){
            throw new PaymentAccountNotSetupException();
        }
        return CheckoutResponse.builder()
                .sessionId(((Session)session).getId())
                .accountId(accountId)
                .build();
    }

    private List<SessionCreateParams.LineItem> createLineItems(OrderDetail header,OrderLine orderLine){
       List<SessionCreateParams.LineItem> lineItems= orderLine.getItems()
                .stream()
               .map(item->{
                   return SessionCreateParams.LineItem.builder()
                           .setAmount(item.getUnitPrice().longValue())
                           .setQuantity(item.getQuantity().longValue())
                           .setCurrency(header.getCurrency().toString())
                           .setName("Gift Card")
                           .build();
               })
               .collect(Collectors.toList());

       lineItems.add(SessionCreateParams.LineItem.builder()
                    .setAmount(orderLine.getTip().longValue())
                    .setQuantity(1L)
                    .setCurrency(header.getCurrency().toString())
                    .setName("Tip")
                    .build()
       );

       return lineItems;
    }

}
