package com.coronacarecard.mapper;


import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

import java.util.List;

public interface BusinessEntityMapper {

    Business toDAO(com.coronacarecard.model.Business business);

    com.coronacarecard.model.Business toModel(Business business);

    BusinessSearchResult toSearchResult(PlacesSearchResult result);

    com.coronacarecard.model.Business toModel(PlaceDetails place);

    PagedBusinessSearchResult toPagedSearchResult(List<BusinessSearchResult> collect,
                                                  int pageNumber, int pageSize, int totalPages);

    BusinessSearchResult toSearchResult(Business p);
}
