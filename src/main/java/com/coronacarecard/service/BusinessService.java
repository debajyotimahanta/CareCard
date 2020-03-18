package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;

import java.util.List;
import java.util.Optional;

public interface BusinessService {

    Business getOrCreate(String id) throws BusinessNotFoundException, InternalException;

    Business createOrUpdate(String id) throws BusinessNotFoundException, InternalException;

    List<BusinessSearchResult> externalSearch(String searchText, Optional<Double> lat, Optional<Double> lng) throws InternalException;

    PagedBusinessSearchResult search(String searchText, int pageNumber, int pageSize);
}
