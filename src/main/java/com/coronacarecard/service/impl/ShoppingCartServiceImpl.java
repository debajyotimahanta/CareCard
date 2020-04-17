package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.dao.OrderDetailRepository;
import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.GiftCard;
import com.coronacarecard.dao.entity.OrderItem;
import com.coronacarecard.exceptions.*;
import com.coronacarecard.mapper.OrderDetailMapper;
import com.coronacarecard.model.CheckoutResponse;
import com.coronacarecard.model.GiftCardState;
import com.coronacarecard.model.PaymentSystem;
import com.coronacarecard.model.orders.Item;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import com.coronacarecard.model.orders.OrderStatus;
import com.coronacarecard.service.PaymentService;
import com.coronacarecard.service.ShoppingCartService;
import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private static final Logger log = LogManager.getLogger(ShoppingCartServiceImpl.class);

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    @Qualifier("StripePaymentService")
    private PaymentService paymentService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public CheckoutResponse checkout(PaymentSystem paymentSystem, OrderDetail order) throws
            BusinessNotFoundException, PaymentAccountNotSetupException, InternalException, PaymentServiceException {

        paymentService.validate(order);
        com.coronacarecard.dao.entity.OrderDetail savedOrder = saveOrder(order);
        order.setId(savedOrder.getId());

        CheckoutResponse response= paymentService.generateCheckoutSession(order);
        savedOrder.toBuilder().sessionId(response.getSessionId());

        orderDetailRepository.save(savedOrder.toBuilder().sessionId(response.getSessionId()).build());
        return response;
    }

    @Override
    public OrderDetail getOrder(UUID id) throws OrderNotFoundException {
        Optional<com.coronacarecard.dao.entity.OrderDetail> orderDAO = orderDetailRepository.findById(id);
        if(!orderDAO.isPresent()) {
            log.error(String.format("Order with id %s not found", id));
            throw new OrderNotFoundException();
        }

        return orderDetailMapper.toOrder(orderDAO.get());
    }

    @VisibleForTesting
    public com.coronacarecard.dao.entity.OrderDetail saveOrder(OrderDetail order) throws BusinessNotFoundException {
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
            Optional<Business> business = businessRepository.findByExternalId(line.getBusinessId());
            if (!business.isPresent()) {
                log.error(String.format("Cannot find business for %s", line.getBusinessId()));
                throw new BusinessNotFoundException("Business not registered with us. Please contact the administrator.");
            }

            OrderItem orderItem =
                    OrderItem.builder()
                            .business(business.get())
                            .tip(line.getTip())
                            .orderDetail(orderDetail)
                            .build();

            orderDetail.addOrderItem(orderItem);
            for (Item item : line.getItems()) {
                com.coronacarecard.dao.entity.Item itemDAO = com.coronacarecard.dao.entity.Item.builder()
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build();
                for (int i = 0; i < item.getQuantity() ; i++) {
                   itemDAO.addGiftCard(GiftCard.builder()
                           .amount(item.getUnitPrice())
                           .state(GiftCardState.Draft)
                           .build());
                }
                orderItem.addItem(itemDAO);
            }
        }

        return orderDetailRepository.save(orderDetail);

    }

}
