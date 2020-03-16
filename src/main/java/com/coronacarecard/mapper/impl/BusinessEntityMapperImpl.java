package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.dao.entity.Contact;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.BusinessSearchResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.stereotype.Component;

@Component
public class BusinessEntityMapperImpl implements BusinessEntityMapper {
    @Override
    public Business toDAO(com.coronacarecard.model.Business business) {
        return null;
    }

    @Override
    public com.coronacarecard.model.Business toModel(Business business) {
        return null;
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
}
