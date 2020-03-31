package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.BusinessAccountDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface BusinessAccountDetailRepository extends PagingAndSortingRepository<BusinessAccountDetail, Long>,
        JpaSpecificationExecutor<BusinessAccountDetail> {

    @Query("SELECT b FROM Business b JOIN FETCH b.owner o JOIN FETCH o.account a WHERE b.id = (:id)")
    Business findBusiness(UUID id);
}
