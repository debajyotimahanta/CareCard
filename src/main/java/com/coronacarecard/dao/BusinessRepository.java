package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends PagingAndSortingRepository<Business, UUID>, JpaSpecificationExecutor<Business> {

    @Query("Select b from Business b where b.name LIKE  %?1% order by b.id")
    Page<Business> findByName(String name, Pageable pageable);

    @Query("Select b from Business b where b.externalRefId =  ?1")
    Optional<Business> findByExternalId(String id);

    @Query("Select b from Business b where (((acos(sin((?1*pi()/180)) * sin((b.latitude*pi()/180))+cos((?2*pi()/180))*cos((b.longitude*pi()/180))*cos(((?2-b.longitude)*pi()/180))))*180/pi())*60*1.1515*1609.344) < ?3")
    List<Business> findOnRadius(Optional<Double> lat, Optional<Double> lng, Optional<Double> radius);
}
