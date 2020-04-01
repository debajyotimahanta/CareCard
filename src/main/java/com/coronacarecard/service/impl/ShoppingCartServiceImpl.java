package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.OrderItem;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.exceptions.PaymentAccountNotSetupException;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.Item;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.model.orders.OrderStatus;
import com.coronacarecard.service.PaymentService;
import com.coronacarecard.service.ShoppingCartService;
import com.coronacarecard.service.payment.PaymentServiceFactory;
import com.stripe.model.checkout.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static Log log = LogFactory.getLog(ShoppingCartServiceImpl.class);

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @Override
    @Transactional
    public CheckoutResponse checkout(PaymentSystem paymentSystem, OrderDetail order) throws BusinessNotFoundException, PaymentAccountNotSetupException, InternalException {

        paymentService.validate(order);
        com.coronacarecard.dao.entity.OrderDetail savedOrder = saveOrder(order);
        order.setId(savedOrder.getId());

        CheckoutResponse response= paymentService.generateCheckoutSession(order);
        savedOrder.toBuilder().sessionId(response.getSessionId());

        orderDetailRepository.save(savedOrder.toBuilder().sessionId(response.getSessionId()).build());
        return response;
    }

    private com.coronacarecard.dao.entity.OrderDetail saveOrder(OrderDetail order) throws BusinessNotFoundException {
        com.coronacarecard.dao.entity.OrderDetail orderDetail =
                com.coronacarecard.dao.entity.OrderDetail.builder()
                        .contribution(order.getContribution())
                        .currency(order.getCurrency())
                        .customerEmail(order.getCustomerEmail())
                        .customerMobile(order.getCustomerMobile())
                        .processingFee(order.getProcessingFee())
                        .total(order.getTotal())
                        .status(order.getStatus()!=null?order.getStatus(): OrderStatus.PENDING)
                        .build();

        for (OrderLine line : order.getOrderLine()) {
            Optional<Business> business = businessRepository.findById(line.getBusinessId());
            if (!business.isPresent()) {
                log.error(String.format("Cannot find business for %s", line.getBusinessId()));
                throw new BusinessNotFoundException();
            }

            OrderItem orderItem =
                    OrderItem.builder()
                            .business(business.get())
                            .tip(line.getTip())
                            .orderDetail(orderDetail)
                            .build();

            orderDetail.addOrderItem(orderItem);
            for (Item item : line.getItems()) {
                orderItem.addItem(com.coronacarecard.dao.entity.Item.builder()
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build());
            }
        }

        return orderDetailRepository.save(orderDetail);

    }

}
