package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Item;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemsRepository
        extends PagingAndSortingRepository<Item, Long>, JpaSpecificationExecutor<Item> {
}
