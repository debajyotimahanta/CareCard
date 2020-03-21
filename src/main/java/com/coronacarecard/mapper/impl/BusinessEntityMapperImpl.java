package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessEntityMapperImpl implements BusinessEntityMapper {
    @Override
    public Business toDAO(com.coronacarecard.model.Business business) {
        String photoUrl       = business.getPhoto() != null ? business.getPhoto().getPhotoUrl() : null;
        String photoReference = business.getPhoto() != null ? business.getPhoto().getPhotoReference() : null;
        String photoAttribution = business.getPhoto() != null && business.getPhoto().getPhotoAttributions() != null
                ? business.getPhoto().getPhotoAttributions()[0] : null;

        return Business.builder()
                .id(business.getId())
                .name(business.getName())
                .latitude(business.getLatitude())
                .longitude(business.getLongitude())
                .address(business.getAddress())
                .photoReference(photoReference)
                .photoUrl(photoUrl)
                .photoAttributions(photoAttribution)
                .Website(business.getWebsite())
                .internationalPhoneNumber(business.getInternationalPhoneNumber())
                .formattedPhoneNumber(business.getFormattedPhoneNumber())
                .build();
    }

    @Override
    public com.coronacarecard.model.Business toModel(Business business) {

        return com.coronacarecard.model.Business.builder()
                .id(business.getId())
                .name(business.getName())
                .photo(Photo.builder()
                        .photoUrl(business.getPhotoUrl())
                        .photoReference(business.getPhotoReference())
                        .build())
                .longitude(business.getLongitude())
                .latitude(business.getLatitude())
                .address(business.getAddress())
                .Website(business.getWebsite())
                .internationalPhoneNumber(business.getInternationalPhoneNumber())
                .formattedPhoneNumber(business.getFormattedPhoneNumber())
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
        // Get the photo reference of the first photo for the business place from Google.
        return com.coronacarecard.model.Business.builder()
                .longitude(place.geometry.location.lng)
                .id(place.placeId)
                .latitude(place.geometry.location.lat)
                .name(place.name)
                .address(place.formattedAddress)
                .photo(Photo.builder()
                        .photoReference(place.photos[0].photoReference)
                        .photoAttributions(place.photos[0].htmlAttributions)
                        .build())
                .internationalPhoneNumber(place.internationalPhoneNumber)
                .formattedPhoneNumber(place.formattedPhoneNumber)
                .build();

    }

    @Override
    public PagedBusinessSearchResult toPagedSearchResult(List<BusinessSearchResult> items,
                                                         int pageNumber, int pageSize, int totalPages) {
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

    private String getPhotoReferenceIfNotNull(com.google.maps.model.Photo[] photos) {
        if (photos != null && photos.length > 0) {
            return photos[0].photoReference;
        }
        return null;
    }

    private String[] getPhotoAttributionsIfNotNull(com.google.maps.model.Photo[] photos) {
        if (photos != null && photos.length > 0) {
            return photos[0].htmlAttributions;
        }
        return null;
    }
}
