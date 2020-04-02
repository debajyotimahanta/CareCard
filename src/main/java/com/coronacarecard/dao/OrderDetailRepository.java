package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrderDetailRepository
        extends PagingAndSortingRepository<OrderDetail, Long>, JpaSpecificationExecutor<OrderDetail> {

    @Query("SELECT o FROM OrderDetail o JOIN FETCH o.orderItems WHERE o.id = (:id)")
    public OrderDetail findByIdAndFetchEagrly(@Param("id") UUID id);
}
