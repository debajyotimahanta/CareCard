package com.coronacarecard.service.impl;

import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.service.GooglePlaceService;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.FindPlaceFromText;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public List<BusinessSearchResult> search(String searchText, Double lat, Double lng) throws InternalException {
        FindPlaceFromTextRequest request = PlacesApi.findPlaceFromText(context, searchText,
                FindPlaceFromTextRequest.InputType.TEXT_QUERY);

        request.fields(FindPlaceFromTextRequest.FieldMask.FORMATTED_ADDRESS, FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                FindPlaceFromTextRequest.FieldMask.NAME, FindPlaceFromTextRequest.FieldMask.PLACE_ID);

        if(!Objects.isNull(lat) && !Objects.isNull(lng)) {
            FindPlaceFromTextRequest.LocationBiasPoint locationBiasPoint =
                    new FindPlaceFromTextRequest.LocationBiasPoint(new LatLng(lat, lng));
            request.locationBias(locationBiasPoint);
        }

        //TODO make this async
        FindPlaceFromText result;
        try {
            result = request.await();
            // Handle successful request.
        } catch (Exception e) {
            log.error(e);
            throw new InternalException("Unable to search place");
        }
        return Arrays.stream(result.candidates).map(t->mapper.toSearchResult(t)).collect(Collectors.toList());
        
    }

}
