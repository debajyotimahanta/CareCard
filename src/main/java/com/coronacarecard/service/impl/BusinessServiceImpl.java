package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.GooglePlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private GooglePlaceService googlePlaceService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private NotificationSender<Business> notificationSender;

    @Override
    public Business getOrCreate(String id) throws BusinessNotFoundException, InternalException {

        Optional<com.coronacarecard.dao.entity.Business> existingBusiness = businessRepository.findByExternalId(id);
        if (existingBusiness.isPresent()) {
            return businessEntityMapper.toModel(existingBusiness.get());
        }

        final Business business = createOrUpdate(id);
        notificationSender.sendNotification(NotificationType.NEW_BUSINESS_REGISTERED, business);
        return business;
    }

    @Override
    public Business createOrUpdate(String id) throws BusinessNotFoundException, InternalException {
        final Business business = googlePlaceService.getBusiness(id);
        com.coronacarecard.dao.entity.Business businessDAO = businessEntityMapper.toDAO(business);
        Optional<com.coronacarecard.dao.entity.Business> existingBusiness = businessRepository.findByExternalId(id);
        if(existingBusiness.isPresent()) {
            businessDAO = businessDAO.toBuilder().id(existingBusiness.get().getId()).build();
        }
        com.coronacarecard.dao.entity.Business savedBusinessDAO = businessRepository.save(businessDAO);
        return business.toBuilder().id(savedBusinessDAO.getId()).build();
    }

    @Override
    public List<BusinessSearchResult> externalSearch(String searchText, Optional<Double> lat, Optional<Double> lng) throws InternalException {
        return googlePlaceService.search(searchText, lat, lng);
    }

    @Override
    public PagedBusinessSearchResult search(String searchText, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<com.coronacarecard.dao.entity.Business> response = businessRepository.findByName(searchText, pageable);

        return businessEntityMapper.toPagedSearchResult(
                response.get().map(p -> businessEntityMapper.toSearchResult(p)).collect(Collectors.toList()),
                response.getPageable().getPageNumber(),
                response.getPageable().getPageSize(),
                response.getTotalPages()
        );
    }
}
