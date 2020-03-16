package com.coronacarecard.mapper;


import com.coronacarecard.dao.entity.Business;

public interface BusinessEntityMapper {

    Business toDAO(com.coronacarecard.model.Business business);

    com.coronacarecard.model.Business toModel(Business business);
}
