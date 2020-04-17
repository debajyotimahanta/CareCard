package com.coronacarecard.mapper.impl;

import com.coronacarecard.dao.entity.Business;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.*;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BusinessEntityMapperImpl implements BusinessEntityMapper {
    private static final Logger log = LogManager.getLogger(BusinessEntityMapperImpl.class);

    @Override
    public Business toDAO(com.coronacarecard.model.Business business) {
        return toDAOBuilder(business).build();
    }

    @Override
    public Business.BusinessBuilder toDAOBuilder(com.coronacarecard.model.Business business) {
        String photoUrl = business.getPhoto() != null ? business.getPhoto().getPhotoUrl() : null;
        String photoReference = business.getPhoto() != null ? business.getPhoto().getPhotoReference() : null;
        String photoAttribution = business.getPhoto() != null && business.getPhoto().getPhotoAttributions() != null
                ? business.getPhoto().getPhotoAttributions()[0] : null;

        return Business.builder()
                .id(business.getId())
                .externalRefId(business.getExternalRefId())
                .name(business.getName())
                .latitude(business.getLatitude())
                .longitude(business.getLongitude())
                .address(business.getAddress())
                .photoReference(photoReference)
                .description(business.getDescription())
                .photoUrl(photoUrl)
                .photoAttributions(photoAttribution)
                .Website(business.getWebsite())
                .internationalPhoneNumber(business.getInternationalPhoneNumber())
                .formattedPhoneNumber(business.getFormattedPhoneNumber());
    }

    @Override
    public com.coronacarecard.model.Business toModel(Business business) {

        return com.coronacarecard.model.Business.builder()
                .id(business.getId())
                .externalRefId(business.getExternalRefId())
                .name(business.getName())
                .photo(Photo.builder()
                        .photoUrl(business.getPhotoUrl())
                        .photoReference(business.getPhotoReference())
                        .photoAttributions(new String[]{business.getPhotoAttributions()})
                        // TODO (arun) add height and width
                        .build())
                .longitude(business.getLongitude())
                .latitude(business.getLatitude())
                .address(business.getAddress())
                .description(business.getDescription())
                .status(business.getState())
                .Website(business.getWebsite())
                .internationalPhoneNumber(business.getInternationalPhoneNumber())
                .formattedPhoneNumber(business.getFormattedPhoneNumber())
                .isActive(business.getState() == BusinessState.Active)
                .owner(buildUserData(business))
                .build();
    }

    @Override
    public BusinessSearchResult toSearchResult(PlacesSearchResult result) {

        return BusinessSearchResult.builder()
                .address(result.formattedAddress)
                .name(result.name)
                .externalRefId(result.placeId)
                .latitude(result.geometry.location.lat)
                .longitude(result.geometry.location.lng)
                .build();
    }

    @Override
    public com.coronacarecard.model.Business toModel(PlaceDetails place) {
        // Get the photo reference of the first photo for the business place from Google.
        return com.coronacarecard.model.Business.builder()
                .longitude(place.geometry.location.lng)
                .externalRefId(place.placeId)
                .latitude(place.geometry.location.lat)
                .name(place.name)
                .address(place.formattedAddress)
                .photo(Photo.builder()
                        .photoReference(place.photos[0].photoReference)
                        .photoAttributions(place.photos[0].htmlAttributions)
                        .height(place.photos[0].height)
                        .width(place.photos[0].width)
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
                .externalRefId(item.getExternalRefId())
                .latitude(item.getLatitude())
                .longitude(item.getLongitude())
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

    private Optional<User> buildUserData(Business business) {
        if (business.getOwner() != null) {
            com.coronacarecard.dao.entity.User user = business.getOwner();
            return Optional.of(User.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .businessAccountDetail(buildBusinessAccountData(user))
                    .build());
        }

        return Optional.empty();
    }

    private Optional<BusinessAccountDetail> buildBusinessAccountData(com.coronacarecard.dao.entity.User user) {
        if (user.getAccount() != null) {
            com.coronacarecard.dao.entity.BusinessAccountDetail account = user.getAccount();
            return Optional.of(BusinessAccountDetail.builder()
                    .externalRefId(account.getExternalRefId())
                    .id(account.getId())
                    .build()
            );
        }

        return Optional.empty();
    }
}
