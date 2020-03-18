package com.coronacarecard.service.impl;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.service.GooglePlaceService;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GooglePlaceServiceImpl implements GooglePlaceService {
    private static Log log = LogFactory.getLog(GooglePlaceServiceImpl.class);

    @Autowired
    private BusinessEntityMapper mapper;

    private final GeoApiContext context;

    public GooglePlaceServiceImpl() {
        //TODO Move key to config
        context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCt-BrVmt0PVN6bAIxABtoGmNVQfjWHM3o")
                .build();
    }

    @Override
    public Business getBusiness(String id) throws BusinessNotFoundException, InternalException {
        PlaceDetailsRequest request = PlacesApi.placeDetails(context, id);
        //TODO make this async
        PlaceDetails result;
        try {
            result = request.await();
            // Handle successful request.
        } catch (Exception e) {
            log.error(e);
            throw new InternalException("Unable to search place");
        }

        return mapper.toModel(result);
    }

    @Override
    public List<BusinessSearchResult> search(String searchText, Optional<Double> lat, Optional<Double> lng) throws InternalException {

        TextSearchRequest request;


        if (lat.isPresent() && lng.isPresent()) {
            request = PlacesApi.textSearchQuery(context, searchText, new LatLng(lat.get(), lng.get()));
        } else {
            request = PlacesApi.textSearchQuery(context, searchText);
        }

        //TODO make this async
        PlacesSearchResponse result;
        try {
            result = request.await();
            // Handle successful request.
        } catch (Exception e) {
            log.error(e);
            throw new InternalException("Unable to search place");
        }
        return Arrays.stream(result.results).map(t -> mapper.toSearchResult(t)).collect(Collectors.toList());

    }
}
