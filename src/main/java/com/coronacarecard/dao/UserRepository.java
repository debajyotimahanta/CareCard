package com.coronacarecard.dao;

import com.coronacarecard.dao.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    User findByConfirmationToken(String confirmationToken);

}
