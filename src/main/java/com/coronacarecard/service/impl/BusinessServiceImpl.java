package com.coronacarecard.service.impl;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.coronacarecard.dao.BusinessRepository;
import com.coronacarecard.exceptions.BusinessNotFoundException;
import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.BusinessEntityMapper;
import com.coronacarecard.model.Business;
import com.coronacarecard.model.BusinessSearchResult;
import com.coronacarecard.model.PagedBusinessSearchResult;
import com.coronacarecard.service.AWSS3Service;
import com.coronacarecard.service.BusinessService;
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
    private AWSS3Service awss3Service;

    private static final String AWS_BUCKET_NAME  = "hjqurnwjjwhb";
    private static final int    PHOTO_MAX_HEIGHT = 400; // value in pixel
    private static final int    PHOTO_MAX_WIDTH  = 450; // value in pixel

    @Override
    public Business create(String id) throws BusinessNotFoundException, InternalException {

        Optional<com.coronacarecard.dao.entity.Business> existingBusiness = Optional.empty();
        if (existingBusiness.isPresent()) {
            return businessEntityMapper.toModel(existingBusiness.get());
        }

        Business business = googlePlaceService.getBusiness(id);

        if (business.getPhoto() != null && business.getPhoto().getPhotoReference() != null) {
            // Get the image from Google Places and Store in Amazon S3
            storeDefaultBusinessImage(business);
        }

        com.coronacarecard.dao.entity.Business businessDAO = businessEntityMapper.toDAO(business);
        businessRepository.save(businessDAO);
        return business;

    }

    @Override
    public List<BusinessSearchResult> externalSearch(String searchText, Optional<Double> lat, Optional<Double> lng) throws InternalException {
        return googlePlaceService.search(searchText, lat, lng);
    }

    @Override
    public PagedBusinessSearchResult search(String searchText, int pageNumber, int pageSize) {
        Sort                                         sort     = Sort.by(Sort.Direction.DESC, "id");
        Pageable                                     pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<com.coronacarecard.dao.entity.Business> response = businessRepository.findByName(searchText, pageable);

        return businessEntityMapper.toPagedSearchResult(
                response.get().map(p -> businessEntityMapper.toSearchResult(p)).collect(Collectors.toList()),
                response.getPageable().getPageNumber(),
                response.getPageable().getPageSize(),
                response.getTotalPages()
        );
    }

    private void storeDefaultBusinessImage(Business business) throws InternalException {
        ImageResult photo = googlePlaceService.getPhoto(business.getPhoto().getPhotoReference(),
                Optional.of(PHOTO_MAX_HEIGHT),
                Optional.of(PHOTO_MAX_WIDTH));
        String imageExtension = getImageExtensionFromContentType(photo.contentType);
        String imageName = new StringBuilder("")
                .append(business.getPhoto().getPhotoReference())
                .append(imageExtension)
                .toString();
        PutObjectResult s3ImageObject = awss3Service.uploadImage(AWS_BUCKET_NAME,
                imageName,
                photo.imageData,
                Optional.of(photo.contentType));
        String s3PhotoUrl = awss3Service.getObjectUrl(AWS_BUCKET_NAME, imageName);
        business.getPhoto().setPhotoUrl(s3PhotoUrl);
    }

    private String getImageExtensionFromContentType(String contentType) {
        return contentType.substring(contentType.indexOf("/")).replace("/", ".");
    }
}
