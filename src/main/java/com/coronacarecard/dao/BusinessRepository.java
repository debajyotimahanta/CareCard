package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Business;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BusinessRepository extends PagingAndSortingRepository<Business, String>, JpaSpecificationExecutor<Business> {
}
