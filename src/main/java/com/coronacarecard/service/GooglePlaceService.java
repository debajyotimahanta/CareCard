package com.coronacarecard.service;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.google.maps.ImageResult;

import java.util.List;
import java.util.Optional;

public interface GooglePlaceService {

    Business getBusiness(String id) throws BusinessNotFoundException, InternalException;

    List<BusinessSearchResult> search(String searchText, Optional<Double> lat, Optional<Double> lng) throws InternalException;

    List<BusinessSearchResult> search(String searchText, Optional<Double> lat, Optional<Double> lng, int radius) throws InternalException;

    ImageResult getPhoto(String photoReference) throws InternalException;


}
