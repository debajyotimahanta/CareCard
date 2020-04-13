package com.coronacarecard.mapper;

import com.coronacarecard.model.orders.OrderDetail;

public interface OrderDetailMapper {

    OrderDetail toOrder(com.coronacarecard.dao.entity.OrderDetail orderDAO);
}
