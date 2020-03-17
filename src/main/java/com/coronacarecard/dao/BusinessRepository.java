package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BusinessRepository extends PagingAndSortingRepository<Business, String>, JpaSpecificationExecutor<Business> {

    @Query("Select b from Business b where b.name LIKE  %?1% order by b.id")
    Page<Business> findByName(String name, Pageable pageable);
}
