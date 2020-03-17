package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.Contact;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.mapper.validation.ValidateCondition;
import com.coronacarecard.mapper.validation.impl.NotNegativeValidation;
import com.coronacarecard.mapper.validation.impl.PageNumberLessThanTotalValidation;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessEntityMapperImpl implements BusinessEntityMapper {
    @Override
    public Business toDAO(com.coronacarecard.model.Business business) {

        return Business.builder()
                .id(business.getId())
                .name(business.getName())
                .latitude(business.getLatitude())
                .longitude(business.getLongitude())
                .address(business.getAddress())
                .photoUrl(business.getPhotoUrl())
                .contact(Contact.builder()
                        .id(business.getContact().getId())
                        .Website(business.getContact().getWebsite())
                        .internationalPhoneNumber(business.getContact().getInternationalPhoneNumber())
                        .formattedPhoneNumber(business.getContact().getFormattedPhoneNumber())
                        .build())
                .build();
    }

    @Override
    public com.coronacarecard.model.Business toModel(Business business) {

        return com.coronacarecard.model.Business.builder()
                .id(business.getId())
                .name(business.getName())
                .photoUrl(business.getPhotoUrl())
                .longitude(business.getLongitude())
                .latitude(business.getLatitude())
                .address(business.getAddress())
                .contact(business.getContact()) // TODO Should we create a Contact Model instead of using DAO?
                .build();
    }

    @Override
    public BusinessSearchResult toSearchResult(PlacesSearchResult result) {

        return BusinessSearchResult.builder()
                .address(result.formattedAddress)
                .name(result.name)
                .id(result.placeId)
                .build();
    }

    @Override
    public com.coronacarecard.model.Business toModel(PlaceDetails place) {
        return com.coronacarecard.model.Business.builder()
                .longitude(place.geometry.location.lng)
                .id(place.placeId)
                .latitude(place.geometry.location.lat)
                .name(place.name)
                .address(place.formattedAddress)
                //TODO Figure out photo
                //.photoUrl()
                .contact(Contact.builder()
                        .internationalPhoneNumber(place.internationalPhoneNumber)
                        .formattedPhoneNumber(place.formattedPhoneNumber)
                        .build())
                .build();

    }

    @SneakyThrows
    @Override
    public PagedBusinessSearchResult toPagedSearchResult(List<BusinessSearchResult> items,
                                                         int pageNumber, int pageSize, int totalPages) {

        // Validate the input values to ensure they don't break the paging logic
        // 1. pageNumber cannot be less than 0
        // 2. pageNumber cannot be greater than totalPages

        List<ValidateCondition> conditions = new ArrayList<ValidateCondition>();
        conditions.add(new NotNegativeValidation(pageNumber));
        conditions.add(new PageNumberLessThanTotalValidation(pageNumber, totalPages));
        for(ValidateCondition condition : conditions) {
            condition.validate();
        }

        return PagedBusinessSearchResult.builder()
                .items(items)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public BusinessSearchResult toSearchResult(Business item) {
        return BusinessSearchResult.builder()
                .address(item.getAddress())
                .name(item.getName())
                .id(item.getId())
                .build();
    }
}
