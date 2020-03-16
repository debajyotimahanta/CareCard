package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.Contact;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends PagingAndSortingRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {
}
