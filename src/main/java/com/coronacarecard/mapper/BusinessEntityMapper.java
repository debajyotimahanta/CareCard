package com.coronacarecard.mapper;


import com.coronacarecard.dao.entity.Business;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

public interface BusinessEntityMapper {

    Business toDAO(com.coronacarecard.model.Business business);

    com.coronacarecard.model.Business toModel(Business business);

    com.coronacarecard.model.BusinessSearchResult toSearchResult(PlacesSearchResult result);

    com.coronacarecard.model.Business toModel(PlaceDetails place);
}
