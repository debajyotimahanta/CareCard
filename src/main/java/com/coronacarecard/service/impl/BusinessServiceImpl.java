package com.coronacarecard.service.impl;

import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.notifications.NotificationSender;
import com.coronacarecard.notifications.NotificationType;
import com.coronacarecard.service.BusinessService;
import com.coronacarecard.service.CloudStorageService;
import com.coronacarecard.service.GooglePlaceService;
import com.google.maps.ImageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private GooglePlaceService googlePlaceService;

    @Autowired
    private BusinessEntityMapper businessEntityMapper;

    @Autowired
    private CloudStorageService cloudStorageService;

    private static final String AWS_BUCKET_NAME  = "hjqurnwjjwhb";
//    private static final int    PHOTO_MAX_HEIGHT = 400; // value in pixel
//    private static final int    PHOTO_MAX_WIDTH  = 450; // value in pixel

    @Autowired
    private NotificationSender<Business> notificationSender;

    @Override
    public Business getOrCreate(String id) throws BusinessNotFoundException, InternalException {
        Optional<com.coronacarecard.dao.entity.Business> existingBusiness = businessRepository.findByExternalId(id);
        if (existingBusiness.isPresent()) {
            return businessEntityMapper.toModel(existingBusiness.get());
        }

        return createOrUpdate(id, existingBusiness);
    }

    @Override
    public Business getBusiness(String externalId) throws BusinessNotFoundException ,InternalException{
        return getOrCreate(externalId);
    }

    @Override
    public Business getBusiness(Long id) throws BusinessNotFoundException  {
        Optional<com.coronacarecard.dao.entity.Business> businessDAO=businessRepository.findById(id);
        if(!businessDAO.isPresent()){
            throw new BusinessNotFoundException();
        }
        return businessEntityMapper.toModel(businessDAO.get());
    }

    // TODO (deba) this is too complicated refactor it.
    @Override
    public Business createOrUpdate(String id) throws BusinessNotFoundException, InternalException {
        return createOrUpdate(id, businessRepository.findByExternalId(id));
    }

    private Business createOrUpdate(String id, Optional<com.coronacarecard.dao.entity.Business> existingBusiness)
            throws BusinessNotFoundException, InternalException {
        Business business = googlePlaceService.getBusiness(id);

        if (business.getPhoto() != null && business.getPhoto().getPhotoReference() != null) {
            // Get the image from Google Places and Store in Amazon S3
            business = storeDefaultBusinessImage(business);
        }

        com.coronacarecard.dao.entity.Business businessDAO = businessEntityMapper.toDAO(business);
        if (existingBusiness.isPresent()) {
            businessDAO = businessDAO.toBuilder().id(existingBusiness.get().getId()).build();
        } else {
            notificationSender.sendNotification(NotificationType.NEW_BUSINESS_REGISTERED, business);
        }
        com.coronacarecard.dao.entity.Business savedBusinessDAO = businessRepository.save(businessDAO);
        return business.toBuilder().id(savedBusinessDAO.getId()).build();
    }

    @Override
    public List<BusinessSearchResult> externalSearch(String searchText, Optional<Double> lat, Optional<Double> lng)
            throws InternalException {
        return googlePlaceService.search(searchText, lat, lng);
    }

    @Override
    public PagedBusinessSearchResult search(String searchText, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<com.coronacarecard.dao.entity.Business> response = businessRepository.findByName(searchText, pageable);

        return businessEntityMapper.toPagedSearchResult(
                response.get().map(p -> businessEntityMapper.toSearchResult(p)).collect(Collectors.toList()),
                response.getPageable().getPageNumber(),
                response.getPageable().getPageSize(),
                response.getTotalPages()
        );
    }

    private Business storeDefaultBusinessImage(Business business) throws InternalException {
        ImageResult photo = googlePlaceService.getPhoto(business.getPhoto().getPhotoReference(),
                Optional.of(business.getPhoto().getHeight()),
                Optional.of(business.getPhoto().getWidth()));
        String imageExtension = getImageExtensionFromContentType(photo.contentType);
        String imageName = new StringBuilder("")
                .append(business.getPhoto().getPhotoReference())
                .append(imageExtension)
                .toString();
        // Store image
        cloudStorageService.uploadImage(AWS_BUCKET_NAME,
                imageName,
                photo.imageData,
                Optional.of(photo.contentType));

        // Get the image url
        String s3PhotoUrl = cloudStorageService.getObjectUrl(AWS_BUCKET_NAME, imageName);

        return business.toBuilder().photo(business.getPhoto().toBuilder()
                .photoUrl(s3PhotoUrl)
                .build()).build();
    }

    private String getImageExtensionFromContentType(String contentType) {
        return contentType.substring(contentType.indexOf("/")).replace("/", ".");
    }
}
