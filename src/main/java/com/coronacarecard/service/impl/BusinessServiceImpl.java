package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.GooglePlaceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private GooglePlaceService googlePlaceService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Override
    public Business create(String id) throws BusinessNotFoundException, InternalException {

        Optional<com.coronacarecard.dao.entity.Business> existingBusiness = businessRepository.findById(id);
        if(existingBusiness.isPresent()) {
            return businessEntityMapper.toModel(existingBusiness.get());
        }

        Business business = googlePlaceService.getBusiness(id);
        com.coronacarecard.dao.entity.Business businessDAO = businessEntityMapper.toDAO(business);
        businessRepository.save(businessDAO);
        return business;

    }

    @Override
    public List<BusinessSearchResult> search(String searchText, Double lat, Double lng) throws InternalException {
        return googlePlaceService.search(searchText, lat, lng);
    }
}
