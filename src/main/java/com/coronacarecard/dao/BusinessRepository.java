package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends PagingAndSortingRepository<Business, UUID>, JpaSpecificationExecutor<Business> {

    @Query("Select b from Business b where b.name LIKE  %?1% order by b.id")
    Page<Business> findByName(String name, Pageable pageable);

    @Query("Select b from Business b where b.externalRefId =  ?1")
    Optional<Business> findByExternalId(String id);
}
