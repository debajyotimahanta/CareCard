package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;

import java.util.List;

public interface GooglePlaceService {

    Business getBusiness(String id) throws BusinessNotFoundException, InternalException;

    List<BusinessSearchResult> search(String searchText, Double lat, Double lng) throws InternalException;
}
