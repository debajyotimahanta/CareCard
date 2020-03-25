package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderItemsRepository
        extends PagingAndSortingRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
    
}
