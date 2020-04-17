package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.GiftCard;
import com.coronacarecard.dao.entity.Item;
import com.coronacarecard.dao.entity.OrderItem;
import com.coronacarecard.mapper.OrderDetailMapper;
import com.coronacarecard.model.orders.OrderDetail;
import com.coronacarecard.model.orders.OrderLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderDetailMapperImpl implements OrderDetailMapper {
    private static final Logger log = LogManager.getLogger(OrderDetailMapperImpl.class);

    @Override
    public OrderDetail toOrder(com.coronacarecard.dao.entity.OrderDetail orderDAO) {
        return OrderDetail.builder()
                .contribution(orderDAO.getContribution())
                .currency(orderDAO.getCurrency())
                .customerEmail(orderDAO.getCustomerEmail())
                .customerMobile(orderDAO.getCustomerMobile())
                .id(orderDAO.getId())
                .processingFee(orderDAO.getProcessingFee())
                .status(orderDAO.getStatus())
                .total(orderDAO.getTotal())
                .orderLine(orderDAO.getOrderItems().stream()
                        .map(l -> mapOrderLineToOrderItem(l)).collect(Collectors.toList()))
                .build();
    }

    private OrderLine mapOrderLineToOrderItem(OrderItem item) {
        return OrderLine.builder()
                .businessId(item.getBusiness().getExternalRefId())
                .businessName(item.getBusiness().getName())
                .tip(item.getTip())
                .items(item.getItems().stream()
                        .map(i -> mapOrderItemDaoToOrderItem(i)).collect(Collectors.toList()))
                .build();
    }

    private com.coronacarecard.model.orders.Item mapOrderItemDaoToOrderItem(Item item) {
        return com.coronacarecard.model.orders.Item.builder()
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .giftCards(item.getGiftCards().stream().map(g->mapGiftCards(g)).collect(Collectors.toList()))
                .build();
    }

    private com.coronacarecard.model.orders.GiftCard mapGiftCards(GiftCard giftCard) {
        return com.coronacarecard.model.orders.GiftCard.builder()
                .amount(giftCard.getAmount())
                .id(giftCard.getId())
                .state(giftCard.getState())
                .build();
    }
}
